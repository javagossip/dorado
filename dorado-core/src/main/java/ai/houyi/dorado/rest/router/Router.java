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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import ai.houyi.dorado.rest.annotation.Controller;
import ai.houyi.dorado.rest.annotation.HttpMethod;
import ai.houyi.dorado.rest.annotation.Path;
import ai.houyi.dorado.rest.http.HttpRequest;
import ai.houyi.dorado.rest.util.StringUtils;

import static ai.houyi.dorado.rest.util.Assert.*;

/**
 * http路由器，用来注册和匹配路由条目
 */
public class Router {

    private static final String WILDCARD = "*";
    private static final String PATH_SEPARATOR = "/";
    private static final String PATH_VARIABLE_PREFIX = "{";
    private static final String PATH_VARIABLE_SUFFIX = "}";
    private static final Router INSTANCE = new Router();

    private final Set<Route> routes = new HashSet<>();
    private final TrieNode root = new TrieNode();

    private Router() {
    }

    public static Router getInstance() {
        return INSTANCE;
    }

    public void registerRoutesByType(Class<?> type) {
        Controller controller = type.getAnnotation(Controller.class);
        if (controller == null) {
            return;
        }

        Path classLevelPath = type.getAnnotation(Path.class);
        String controllerPath = classLevelPath == null ? StringUtils.EMPTY : classLevelPath.value();

        Method[] controllerMethods = type.getDeclaredMethods();
        for (Method method : controllerMethods) {
            if (Modifier.isStatic(method.getModifiers()) || method.getAnnotations().length == 0 ||
                    !Modifier.isPublic(method.getModifiers())) {
                continue;
            }

            Path methodLevelPath = method.getAnnotation(Path.class);
            HttpMethod httpMethod = getHttpMethod(method.getAnnotations());
            String methodPath = methodLevelPath == null ? StringUtils.EMPTY : methodLevelPath.value();

            String requestPath = String.format("%s%s", controllerPath, methodPath);
            addRoute(new Route(requestPath,
                    httpMethod == null ? null : httpMethod.value(),
                    RouteHandler.create(method)));
        }
    }

    public void addRoute(Route route) {
        routes.add(route);
        TrieNode current = root;
        for (String part : StringUtils.splitTrim(route.getPath(), PATH_SEPARATOR)) {
            ///api/v1/campaigns/{name:\s+}/summary
            if (isPathVariable(part)) {
                if (!current.children.containsKey(part)) {
                    PathVariable pathVariable = PathVariable.of(part);
                    current.children.put(part, TrieNode.create(pathVariable));
                    route.addPathVariable(pathVariable);
                }
            } else if (StringUtils.isRegExp(part)) {
                // /swagger-ui.*
                if (!current.children.containsKey(part)) {
                    current.children.put(part, new TrieNode().regExp(part));
                }
            } else if (!current.children.containsKey(part)) {
                // /api/v1/campaigns
                current.children.put(part, new TrieNode());
            }
            current = current.children.get(part);
        }
        current.isEnd = true;
        current.addRoute(route.getMethod(), route);
    }

    public Route getRoute(HttpRequest request) {
        return getRoute(request.getRequestURI(), request.getMethod().toUpperCase());
    }

    public Route getRoute(String path, String method) {
        notBlank(path, "path must not be blank");
        notBlank(method, "method must not be blank");

        TrieNode current = root;
        for (String part : StringUtils.splitTrim(path, PATH_SEPARATOR)) {
            current = current.child(part);
            if (current == null) {
                return null;
            }
        }
        if (current.isEnd) {
            return current.route(method);
        }
        return null;
    }

    public List<Route> getRoutes() {
        return Collections.unmodifiableList(new ArrayList<>(routes));
    }

    private static boolean isPathVariable(String part) {
        return part.startsWith(PATH_VARIABLE_PREFIX) && part.endsWith(PATH_VARIABLE_SUFFIX);
    }

    private HttpMethod getHttpMethod(Annotation[] annotations) {
        HttpMethod httpMethod;

        for (Annotation annotation : annotations) {
            httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
            if (httpMethod != null) {
                return httpMethod;
            }
        }
        return null;
    }

    public void reset() {
        root.children.clear();
        routes.clear();
    }

    static class TrieNode {

        Map<String, TrieNode> children = new HashMap<>();
        //<GET,xxx> or <POST,xxx> 用来解决相同访问路径，不同http方法对应不同的Route
        Map<String, Route> methodRoutes = new HashMap<>();
        PathVariable pathVariable;
        Pattern pattern;
        boolean isEnd = false;

        public static TrieNode create(PathVariable pathVariable) {
            TrieNode trieNode = new TrieNode();
            trieNode.pathVariable = pathVariable;
            return trieNode;
        }

        public TrieNode child(String pathPart) {
            if (children.containsKey(pathPart)) {
                return children.get(pathPart);
            }
            for (Entry<String, TrieNode> entry : children.entrySet()) {
                TrieNode trieNode = entry.getValue();
                if (trieNode.isPathVariableNode()) {
                    //{id:\s+} or {id}
                    PathVariable pathVariable = trieNode.pathVariable;
                    if (pathVariable.matches(pathPart)) {
                        pathVariable.setValue(pathPart);
                        return entry.getValue();
                    }
                } else if (trieNode.isRegExpNode()) {
                    //如果是一个正则表达式
                    Pattern pattern = trieNode.pattern;
                    if (pattern.matcher(pathPart).matches()) {
                        return trieNode;
                    }
                }
            }
            return null;
        }

        public TrieNode addRoute(String method, Route route) {
            methodRoutes.put(method, route);
            return this;
        }

        public boolean isPathVariableNode() {
            return pathVariable != null;
        }

        public boolean isRegExpNode() {
            return pattern != null;
        }

        public Route route(String method) {
            notBlank(method, "method must not be blank");
            Route route = methodRoutes.get(method);

            return route == null ? methodRoutes.get(WILDCARD) : route;
        }

        public TrieNode regExp(String regExp) {
            notBlank(regExp, "regExp must not be blank");
            this.pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
            return this;
        }
    }
}
