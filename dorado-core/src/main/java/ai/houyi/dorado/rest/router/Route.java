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
import ai.houyi.dorado.rest.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Route {

    private final String path;
    private final String method;
    private final Map<String, PathVariable> pathVariables;
    private final RouteHandler handler;
    private final boolean hasPathVariables;
    private final boolean pathIsRegExp;

    public Route(String path, String method) {
        this(path, method, null);
    }

    public Route(String path, String method, RouteHandler handler) {
        this.path = path;
        this.method = StringUtils.isBlank(method) ? "*" : method;
        this.handler = handler;
        this.pathVariables = new HashMap<>();
        this.hasPathVariables = path.contains("{") && path.contains("}");
        this.pathIsRegExp = StringUtils.isRegExp(path);
    }

    public void addPathVariable(PathVariable variable) {
        pathVariables.put(variable.getName(), variable);
    }

    public boolean containsPathVariable(String name) {
        return pathVariables.containsKey(name);
    }

    public boolean hasPathVariables() {
        return hasPathVariables;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, PathVariable> getPathParameters() {
        return pathVariables;
    }

    public RouteHandler getHandler() {
        return this.handler;
    }

    public PathVariable getPathParameter(String name) {
        return pathVariables.get(name);
    }

    public void setPathVariableValue(String name, String value) {
        PathVariable pathVariable = pathVariables.get(name);
        Assert.notNull(pathVariable, "PathParameter '" + name + "' not found");

        pathVariable.setValue(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Route route = (Route) o;
        return Objects.equals(path, route.path) && Objects.equals(method, route.method) &&
                Objects.equals(pathVariables, route.pathVariables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, method, pathVariables);
    }

    public boolean pathIsRegExp() {
        return this.pathIsRegExp;
    }
}
