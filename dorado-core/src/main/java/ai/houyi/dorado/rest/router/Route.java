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

import ai.houyi.dorado.rest.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Route {

    private final String path;
    private final String method;
    private final Map<String, PathParameter> pathParameters;
    private final RouteHandler handler;

    public Route(String path, String method) {
        this(path, method, null);
    }

    public Route(String path, String method, RouteHandler handler) {
        this.path = path;
        this.method = method;
        this.handler = handler;
        this.pathParameters = new HashMap<>();
    }

    public void addPathParameter(PathParameter variable) {
        pathParameters.put(variable.getName(), variable);
    }

    public boolean containsPathParameter(String name) {
        return pathParameters.containsKey(name);
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, PathParameter> getPathParameters() {
        return pathParameters;
    }

    public RouteHandler getHandler() {
        return this.handler;
    }

    public PathParameter getPathParameter(String name) {
        return pathParameters.get(name);
    }

    public void setPathParameterValue(String pathParameterName, String value) {
        PathParameter pathParameter = pathParameters.get(pathParameterName);
        Assert.notNull(pathParameter, "PathParameter '" + pathParameterName + "' not found");

        pathParameter.setValue(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Route route = (Route) o;
        return Objects.equals(path, route.path) && Objects.equals(method, route.method) &&
                Objects.equals(pathParameters, route.pathParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, method, pathParameters);
    }
}
