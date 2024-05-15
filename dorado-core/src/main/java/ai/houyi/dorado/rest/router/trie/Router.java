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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ai.houyi.dorado.rest.http.HttpRequest;
import ai.houyi.dorado.rest.util.Assert;
import ai.houyi.dorado.rest.util.StringUtils;

/**
 * http路由器，用来注册和匹配路由条目
 */
public class Router {

    private static final String WILDCARD_TRIE_NODE_VALUE = ":";
    private static final String ALL_METHOD = "_ALL_";
    private static final String PATH_SEPARATOR = "/";
    private static final String PATH_VARIABLE_PREFIX = "{";
    private static final String PATH_VARIABLE_SUFFIX = "}";

    private static final Router INSTANCE = new Router();
    private Set<Route> routes = new HashSet<>();

    private final TrieNode root = new TrieNode();

    private Router() {
    }

    public static Router getInstance() {
        return INSTANCE;
    }

    public void addRoute(Route route) {
        routes.add(route);
        TrieNode currentNode = _createOrGetRootNode(route.getMethod());
        for (String part : route.getPath().split(PATH_SEPARATOR)) {
            if (part.startsWith(PATH_VARIABLE_PREFIX) && part.endsWith(PATH_VARIABLE_SUFFIX)) {
                if (!currentNode.children.containsKey(WILDCARD_TRIE_NODE_VALUE)) {
                    String[] pathParts = part.substring(1, part.length() - 1).split(":");
                    String pathParameterName = pathParts[0];
                    String pathParameterPattern = pathParts.length == 2 ? pathParts[1] : null;
                    PathParameter pathParameter = PathParameter.create(pathParameterName, pathParameterPattern);
                    route.addPathParameter(pathParameter);

                    currentNode.children.put(WILDCARD_TRIE_NODE_VALUE, new TrieNode());
                    currentNode.children.get(WILDCARD_TRIE_NODE_VALUE).pathParameterName = pathParameterName;
                }
                currentNode = currentNode.children.get(WILDCARD_TRIE_NODE_VALUE);
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

    private TrieNode _createOrGetRootNode(String method) {
        String _method = StringUtils.isBlank(method) ? ALL_METHOD : method;
        if (!root.children.containsKey(_method)) {
            root.children.put(_method, new TrieNode());
        }
        return root.children.get(_method);
    }

    public Route matchRoute(HttpRequest request) {
        Route route = matchRoute(request.getRequestURI(), request.getMethod().toUpperCase());
        if (route != null) {
            return route;
        }
        return matchRoute(request.getRequestURI(), ALL_METHOD);
    }

    public Route matchRoute(String path, String method) {
        Assert.notBlank(path, "path must not be blank");
        Assert.notBlank(method, "method must not be blank");

        TrieNode currentNode = _createOrGetRootNode(method);
        for (String part : path.split(PATH_SEPARATOR)) {
            if (currentNode.children.containsKey(part)) {
                currentNode = currentNode.children.get(part);
                if (currentNode.isLeaf) {
                    return currentNode.route;
                }
            } else {
                if (currentNode.children.containsKey(WILDCARD_TRIE_NODE_VALUE)) {
                    currentNode = currentNode.children.get(WILDCARD_TRIE_NODE_VALUE);
                    currentNode.route.setPathParameterValue(currentNode.pathParameterName, part);
                    if (currentNode.isLeaf) {
                        return currentNode.route;
                    }
                }
                return null;
            }
        }
        return null;
    }

    public Set<Route> getRoutes() {
        return Collections.unmodifiableSet(this.routes);
    }

    static class TrieNode {

        Map<String, TrieNode> children = new HashMap<>();
        Route route;
        String pathParameterName;
        boolean isLeaf = false;
    }
}
