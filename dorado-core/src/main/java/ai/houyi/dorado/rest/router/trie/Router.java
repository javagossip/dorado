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

package ai.houyi.dorado.rest.router.trie;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ai.houyi.dorado.rest.annotation.HttpMethod;
import ai.houyi.dorado.rest.http.HttpRequest;
import ai.houyi.dorado.rest.util.Assert;
import ai.houyi.dorado.rest.util.StringUtils;

/**
 * http路由器，用来注册和匹配路由条目
 */
public class Router {

    private static final String ANY = ":";

    private final TrieNode root = new TrieNode();
    private static final Map<String, TrieNode> ROOT_NODE_MAP_BY_METHOD = new ConcurrentHashMap<>();
    private static final Router INSTANCE = new Router();

    static {
        ROOT_NODE_MAP_BY_METHOD.put(HttpMethod.GET, new TrieNode());
        ROOT_NODE_MAP_BY_METHOD.put(HttpMethod.POST, new TrieNode());
        ROOT_NODE_MAP_BY_METHOD.put(HttpMethod.PUT, new TrieNode());
        ROOT_NODE_MAP_BY_METHOD.put(HttpMethod.DELETE, new TrieNode());
        ROOT_NODE_MAP_BY_METHOD.put(HttpMethod.HEAD, new TrieNode());
        ROOT_NODE_MAP_BY_METHOD.put(HttpMethod.OPTIONS, new TrieNode());
        ROOT_NODE_MAP_BY_METHOD.put("_ANY_", new TrieNode());
    }

    private Router() {
    }

    public static Router getInstance() {
        return INSTANCE;
    }

    public void addRoute(Route route) {
        String method = StringUtils.isBlank(route.getMethod()) ? "_ANY_" : route.getMethod();
        TrieNode currentNode = ROOT_NODE_MAP_BY_METHOD.get(method);

        for (String part : route.getPath().split("/")) {
            if (part.startsWith("{") && part.endsWith("}")) {
                if (!currentNode.children.containsKey(ANY)) {
                    String[] pathParts = part.substring(1, part.length() - 1).split(":");
                    String pathParameterName = pathParts[0];
                    String pathParameterPattern = pathParts.length == 2 ? pathParts[1] : null;
                    PathParameter pathParameter = PathParameter.create(pathParameterName, pathParameterPattern);
                    route.addPathParameter(pathParameter);
                    currentNode.children.put(ANY, new TrieNode());
                    currentNode.children.get(ANY).pathParameterName = pathParameterName;
                }
                currentNode = currentNode.children.get(ANY);
            } else {
                if (!currentNode.children.containsKey(part)) {
                    currentNode.children.put(part, new TrieNode());
                }
                currentNode = currentNode.children.get(part);
            }
        }
        currentNode.isLeaf = true;
        currentNode.route = route;
    }

    public Route matchRoute(HttpRequest request) {
        Route route = matchRoute(request.getRequestURI(), request.getMethod().toUpperCase());
        if (route != null) {
            return route;
        }
        return matchRoute(request.getRequestURI(), "_ANY_");
    }

    public Route matchRoute(String path, String method) {
        Assert.notBlank(path, "path must not be blank");
        Assert.notBlank(method, "method must not be blank");

        TrieNode currentNode = ROOT_NODE_MAP_BY_METHOD.get(method);
        for (String part : path.split("/")) {
            if (currentNode.children.containsKey(part)) {
                currentNode = currentNode.children.get(part);
                if (currentNode.isLeaf) {
                    return currentNode.route;
                }
            } else {
                if (currentNode.children.containsKey(ANY)) {
                    currentNode = currentNode.children.get(ANY);
                    PathParameter pathParameter = currentNode.route.getPathParameter(currentNode.pathParameterName);
                    if (pathParameter != null) {
                        pathParameter.setValue(part);
                    }
                    if (currentNode.isLeaf) {
                        return currentNode.route;
                    }
                }
                return null;
            }
        }
        return null;
    }

    static class TrieNode {

        Map<String, TrieNode> children = new HashMap<>();
        Route route;
        String pathParameterName;
        boolean isLeaf = false;
    }
}
