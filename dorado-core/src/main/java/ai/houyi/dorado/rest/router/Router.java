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
import ai.houyi.dorado.rest.http.HttpRequest;
import ai.houyi.dorado.rest.http.HttpResponse;
import ai.houyi.dorado.rest.util.Assert;
import ai.houyi.dorado.rest.util.StringUtils;

/**
 * http路由器，用来注册和匹配路由条目
 */
public class Router {

    private static final String WILDCARD = "*";
    private static final String PATH_SEPARATOR = "/";
    private static final String PATH_VARIABLE_PREFIX = "{";
    private static final String PATH_VARIABLE_SUFFIX = "}";

    private final Set<Route> routes = new HashSet<>();
    private final Map<String, TrieNode> methodRoots = new HashMap<>();

    private Router() {
        for (String method : Arrays.asList("GET", "POST", "PUT", "DELETE", WILDCARD)) {
            methodRoots.put(method, new TrieNode());
        }
    }

    public static Router newInstance() {
        return new Router();
    }

    public RouteContext matchRoute(HttpRequest request) {
        RouteContext routeContext = matchRoute(request.getRequestURI(), request.getMethod().toUpperCase());
        if (routeContext != null) {
            return routeContext;
        }
        return matchRoute(request.getRequestURI(), WILDCARD);
    }

    public List<Route> getRoutes() {
        return Collections.unmodifiableList(new ArrayList<>(routes));
    }

    private static boolean isPathVar(String part) {
        return part != null && (part.startsWith(PATH_VARIABLE_PREFIX) && part.endsWith(PATH_VARIABLE_SUFFIX));
    }

    public void addRoute(String path, String httpMethod, RouteHandler handler) {
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
                key = pathVar.pattern == null ? "{var}" : "{var:" + pathVar.regex + "}";
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
        if (current == null) {
            current = methodRoots.get(WILDCARD);
        }
        String[] parts = StringUtils.splitTrim(path, PATH_SEPARATOR);
        Map<String, String> pathVars = new HashMap<>();
        for (String part : parts) {
            current = current.child(part, pathVars);
            if (current == null) {
                return null;
            }
        }
        if (current.route != null) {
            return RouteContext.create(current.route, pathVars);
        }
        return null;
    }

    public void dump() {
        methodRoots.forEach((k, v) -> {
            System.out.println("<<<" + k + ">>>");
            if (!v.children.isEmpty()) {
                System.out.println(v.printString());
            }
        });
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
                //TrieNode trieNode = entry.getValue();
                //eg: path /campaigns/123 应该优先匹配/campaigns/{id:[0-9]+}，其次/campaigns/{id}
                PathVar pathVar = trieNode.pathVar;
                if (pathVar != null && pathVar.match(part)) {
                    pathVars.put(pathVar.name, part);
                    return trieNode;
                }
            }
            //再次匹配普通的路径变量
            for (TrieNode trieNode : pathVarChildren) {
                pathVars.put(trieNode.pathVar.name, part);
                return trieNode;
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

        public String printString() {
            return printNode(this, 0);
        }

        private String printNode(TrieNode node, int level) {
            StringBuilder result = new StringBuilder();
            if (node == null) {
                return null;
            }
            // 打印当前节点，使用缩进表示层级
            result.append("    ".repeat(level) + (StringUtils.isBlank(node.value) ? "/" : node.value));
            result.append("\n");

            // 遍历并打印子节点
            for (Entry<String, TrieNode> entry : node.children.entrySet()) {
                String nodeString = printNode(entry.getValue(), level + 1);
                if (!StringUtils.isBlank(nodeString)) {
                    result.append(nodeString).append("\n");
                }
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
