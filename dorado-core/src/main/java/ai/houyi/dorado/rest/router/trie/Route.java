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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ai.houyi.dorado.rest.util.Assert;

public class Route {

    private final String path;
    private final String method;
    private final List<PathParameter> pathParameters;
    private final RouteHandler handler;

    public Route(String path, String method) {
        this(path, method, null);
    }

    public Route(String path, String method, RouteHandler handler) {
        this.path = path;
        this.method = method;
        this.handler = handler;
        this.pathParameters = new ArrayList<>();
    }

    public void addPathParameter(PathParameter variable) {
        pathParameters.add(variable);
    }

    public boolean containsPathParameter(String name) {
        Assert.notNull(name, "Parameter name must not be null");
        for (PathParameter p : pathParameters) {
            if (name.equals(p.getName())) {
                return true;
            }
        }
        return false;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public List<PathParameter> getPathParameters() {
        return Collections.unmodifiableList(pathParameters);
    }

    public Map<String, PathParameter> getPathParameterMap() {
        return pathParameters.stream()
                .collect(Collectors.toMap(PathParameter::getName, Function.identity(), (a, b) -> a));
    }

    public RouteHandler getHandler() {
        return this.handler;
    }

    public PathParameter getPathParameter(String name) {
        for (PathParameter pathParameter : pathParameters) {
            if (pathParameter.getName().equals(name)) {
                return pathParameter;
            }
        }
        return null;
    }
}
