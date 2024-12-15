/*
 *
 *  * Copyright 2017 The OpenDSP Project
 *  *
 *  * The OpenDSP Project licenses this file to you under the Apache License,
 *  * version 2.0 (the "License"); you may not use this file except in compliance
 *  * with the License. You may obtain a copy of the License at:
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *
 */

package ai.houyi.dorado.rest.router;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import ai.houyi.dorado.exception.DoradoException;
import ai.houyi.dorado.rest.annotation.HttpMethod;
import ai.houyi.dorado.rest.http.HttpRequest;
import ai.houyi.dorado.rest.http.HttpResponse;
import ai.houyi.dorado.rest.util.Assert;
import ai.houyi.dorado.rest.util.LogUtils;
import ai.houyi.dorado.rest.util.StringUtils;

/**
 * http路由器，用来注册和匹配路由条目
 */
public class Router {

    private static final String WILDCARD = "*";
    private static final String PATH_SEPARATOR = "/";
    private static final String PATH_VARIABLE_PREFIX = "{";
    private static final String PATH_VARIABLE_SUFFIX = "}";
    private static final String PATH_VARIABLE_KEY = "{var}";
    private static final List<String> SUPPORTED_METHODS =
            Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE);

    private final Set<Route> routes = new HashSet<>();
    private final Map<String, TrieNode> methodRoots = new HashMap<>();

    private Router() {
        SUPPORTED_METHODS.forEach(method -> methodRoots.put(method, new TrieNode()));
    }

    public static Router newInstance() {
        return new Router();
    }

    public RouteContext matchRoute(HttpRequest request) {
        return matchRoute(request.getRequestURI(), request.getMethod().toUpperCase());
    }

    public List<Route> getRoutes() {
        return Collections.unmodifiableList(new ArrayList<>(routes));
    }

    private static boolean isPathVar(String part) {
        return part != null && (part.startsWith(PATH_VARIABLE_PREFIX) && part.endsWith(PATH_VARIABLE_SUFFIX));
    }

    public void addRoute(String path, String httpMethod, RouteHandler handler) {
        //if support all http methods, add route to all
        if (WILDCARD.equals(httpMethod) || StringUtils.isBlank(httpMethod)) {
            SUPPORTED_METHODS.forEach(method -> addRoute(path, method, handler));
            return;
        }
        Route route = new Route(path, httpMethod, handler);
        TrieNode current = methodRoots.get(route.method);
        if (current == null) {
            throw new DoradoException("Unsupported http method: " + route.method);
        }

        for (String part : StringUtils.splitTrim(route.path, PATH_SEPARATOR)) {
            ///api/v1/campaigns/{name:\s+}/summary
            String key = part;
            if (isPathVar(key)) {
                PathVar pathVar = PathVar.create(part);
                key = pathVar.pattern == null ? PATH_VARIABLE_KEY : "{var:" + pathVar.regex + "}";
                if (!current.hasChild(key)) {
                    current.addChild(key, TrieNode.create(pathVar));
                    current.children.get(key).pathVar = pathVar;
                }
            } else if (!current.hasChild(key)) {
                current.addChild(key, new TrieNode());
            }
            current = current.children.get(key);
        }

        if (current.route != null) {
            throw new DoradoException(
                    "Route conflict: " + route.path + " conflicts with existing route " + current.route.path);
        }
        current.route = route;
        routes.add(route);
    }

    public RouteContext matchRoute(String path, String method) {
        //按照优先级匹配，先精确匹配，其次匹配带正则的路径变量，再匹配普通的路径变量，最后匹配通配符
        TrieNode current = methodRoots.get(method);
        String[] parts = StringUtils.splitTrim(path, PATH_SEPARATOR);

        Map<String, String> pathVars = new HashMap<>();

        TrieNode matchNode = matchNode(current, parts, 0, pathVars);
        if (matchNode != null && matchNode.route != null) {
            return RouteContext.create(matchNode.route, pathVars);
        }
        return null;
    }

    private TrieNode matchNode(TrieNode node, String[] parts, int index, Map<String, String> pathVars) {
        if (index == parts.length) {
            return node.route != null ? node : null;
        }
        String part = parts[index];
        TrieNode child = node.children.get(part);

        // 精确匹配
        if (child != null) {
            TrieNode result = matchNode(child, parts, index + 1, pathVars);
            if (result != null) {
                return result;
            }
        }

        //再次匹配路径变量，按照优先匹配带正则表达式的路径
        for (TrieNode trieNode : node.regexPathVarChildren) {
            //eg: path /campaigns/123 应该优先匹配/campaigns/{id:[0-9]+}，其次/campaigns/{id}
            PathVar pathVar = trieNode.pathVar;
            if (pathVar != null && pathVar.match(part)) {
                pathVars.put(pathVar.name, part);
                TrieNode result = matchNode(trieNode, parts, index + 1, pathVars);
                if (result != null) {
                    return result;
                }
            }
        }
        //再次匹配普通的路径变量
        TrieNode pathVarNode = node.pathVarChildren.isEmpty() ? null : node.pathVarChildren.iterator().next();
        if (pathVarNode != null) {
            pathVars.put(pathVarNode.pathVar.name, part);
            return matchNode(pathVarNode, parts, index + 1, pathVars);
        }
        return null;
    }

    public String dump() {
        StringBuilder result = new StringBuilder();
        methodRoots.forEach((method, v) -> {
            if (!v.children.isEmpty()) {
                result.append("<<<--dump trie for method: ").append(method).append("-->>>").append("\n");
                result.append(v);
                result.append("<<<-- end dump -->>>");
                result.append("\n");
            }
        });
        return result.toString();
    }

    public void handleRequest(HttpRequest request, HttpResponse response) {
        RouteContext routeContext = matchRoute(request);
        if (routeContext == null) {
            response.sendError(HttpResponse.SC_NOT_FOUND);
            response.writeStringUtf8(String.format("Resource not found, url: [%s], http_method: [%s]",
                    request.getRequestURI(),
                    request.getMethod()));
        } else {
            routeContext.route.handler.handle(request, response, routeContext.pathVars);
        }
    }

    static class RouteContext {

        Route route;
        Map<String, String> pathVars;

        RouteContext(Route route, Map<String, String> pathVars) {
            this.route = route;
            this.pathVars = pathVars;
        }

        static RouteContext create(Route route, Map<String, String> pathVars) {
            return new RouteContext(route, pathVars);
        }
    }

    public static class Route {

        String path;
        String method;
        RouteHandler handler;

        public Route(String path, String method, RouteHandler handler) {
            this.path = path;
            this.method = method == null ? WILDCARD : method;
            this.handler = handler;
        }

        public String getPath() {
            return path;
        }

        public String getMethod() {
            return method;
        }
    }

    public static class PathVar {

        String name;
        String regex;
        Pattern pattern;

        public PathVar(String name, String regex) {
            this.name = name;
            this.regex = regex;
            this.pattern = regex == null ? null : Pattern.compile(regex);
        }

        public static PathVar create(String part) {
            Assert.notNull(part, "part must not be null");
            String internalPart = part;
            if (isPathVar(part)) {
                internalPart = part.substring(1, part.length() - 1);
            }
            String[] parts = internalPart.split(":");
            return new PathVar(parts[0], parts.length > 1 ? parts[1] : null);
        }

        public boolean match(String part) {
            return pattern != null && pattern.matcher(part).matches();
        }

        @Override
        public String toString() {
            return "PathVar{" + "name='" + name + '\'' + ", regex='" + regex + '\'' + ", pattern=" + pattern + '}';
        }
    }

    public static class TrieNode {

        Route route;
        PathVar pathVar;
        String value;

        //子节点需要排序，如何排序呢，给TrieNode来个优先级字段？？？
        Map<String, TrieNode> children = new HashMap<>();
        //普通的pathVar子节点
        Set<TrieNode> pathVarChildren = new TreeSet<>(TrieNodeComparator.INSTANCE);
        //带具体正则表达式的pathVar子节点
        Set<TrieNode> regexPathVarChildren = new TreeSet<>(TrieNodeComparator.INSTANCE);

        public TrieNode() {
            this.value = "";
        }

        public static TrieNode create(PathVar pathVar) {
            TrieNode trieNode = new TrieNode();
            trieNode.pathVar = pathVar;
            return trieNode;
        }

        public TrieNode child(String part, Map<String, String> pathVars) {
            //首先精确匹配，如果匹配到直接返回
            if (hasChild(part)) {
                return children.get(part);
            }
            //再次匹配路径变量，按照优先匹配带正则表达式的路径
            for (TrieNode trieNode : regexPathVarChildren) {
                //eg: path /campaigns/123 应该优先匹配/campaigns/{id:[0-9]+}，其次/campaigns/{id}
                PathVar pathVar = trieNode.pathVar;
                if (pathVar != null && pathVar.match(part)) {
                    pathVars.put(pathVar.name, part);
                    return trieNode;
                }
            }
            //再次匹配普通的路径变量, 无正则表达式的环境变量最多只存在一个
            TrieNode pathVarNode = pathVarChildren.isEmpty() ? null : pathVarChildren.iterator().next();
            if (pathVarNode != null) {
                pathVars.put(pathVarNode.pathVar.name, part);
                return pathVarNode;
            }
            return null;
        }

        public void addChild(String key, TrieNode child) {
            children.put(key, child);
            child.value = key;
            if (child.pathVar != null) {
                if (child.pathVar.pattern != null) {
                    regexPathVarChildren.add(child);
                } else {
                    pathVarChildren.add(child);
                }
            }
        }

        public boolean hasChild(String key) {
            return children.containsKey(key);
        }

        @Override
        public String toString() {
            return printNode(this, 0);
        }

        private String printNode(TrieNode node, int level) {
            // 打印当前节点，使用缩进表示层级
            /**
             * eg:
             * ROOT
             *     |--campaigns --> path: </campaigns> handler: <<DUMMY>>
             *         |--123 --> path: </campaigns/123> handler: <<DUMMY>>
             *             |--details --> path: </campaigns/123/details> handler: <<DUMMY>>
             *         |--789
             *             |--detail --> path: </campaigns/789/detail> handler: <<DUMMY>>
             *         |--{var:[0-9]+} --> path: </campaigns/{id:[0-9]+}> handler: <<DUMMY>>
             *         |--{var} --> path: </campaigns/{id}> handler: <<DUMMY>>
             *     |--apidocs
             *         |--swagger.json --> path: </apidocs/swagger.json> handler: <<DUMMY>>
             *         |--swagger.yaml --> path: </apidocs/swagger.yaml> handler: <<DUMMY>>
             */
            StringBuilder result = new StringBuilder();
            result.append(StringUtils.isBlank(node.value)
                    ? "ROOT"
                    : node.value + (node.route == null
                            ? ""
                            : (" --> path: <" + node.route.path + "> handler: " + node.route.handler)));
            result.append("\n");
            // 遍历并打印子节点
            for (Entry<String, TrieNode> entry : node.children.entrySet()) {
                result.append("    ".repeat(level + 1)).append("|--");
                result.append(printNode(entry.getValue(), level + 1));
            }
            return result.toString();
        }
    }

    static class TrieNodeComparator implements Comparator<TrieNode> {

        public static final TrieNodeComparator INSTANCE = new TrieNodeComparator();

        @Override
        public int compare(TrieNode o1, TrieNode o2) {
            PathVar o1PathVar = o1.pathVar;
            PathVar o2PathVar = o2.pathVar;

            //如果变量名称不一致的话，直接按照字典序排序
            if (!o1PathVar.name.equals(o2PathVar.name)) {
                return o1PathVar.name.compareTo(o2PathVar.name);
            }
            // 优先比较 regex 非空性
            boolean o1HasRegex = o1PathVar.regex != null;
            boolean o2HasRegex = o2PathVar.regex != null;

            if (o1HasRegex != o2HasRegex) {
                return o1HasRegex ? -1 : 1;
            }

            // 如果名称相同，比较更具体的 regex
            if (o1HasRegex) {
                return o2PathVar.regex.length() - o1PathVar.regex.length();
            }
            return 0;
        }
    }
}
