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

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

//{name:regexp}
public class PathVariable {

    private final String name;
    private final String regexp;
    private final Pattern pattern;
    /**
     * 实际请求url上路径变量的值
     */
    private String value;

    private PathVariable(String name, String regexp, Pattern pattern) {
        this.name = name;
        this.regexp = regexp;
        this.pattern = pattern;
    }

    public static PathVariable create(String name, String regexp) {
        if (StringUtils.isBlank(regexp)) {
            return new PathVariable(name, regexp, null);
        }
        try {
            Pattern pattern = Pattern.compile(regexp);
            return new PathVariable(name, regexp, pattern);
        } catch (PatternSyntaxException ex) {
            //DO NOTHING
        }
        return new PathVariable(name, regexp, null);
    }

    public static PathVariable of(String pathVariable) {
        Assert.notBlank(pathVariable, "pathVariable must not be blank");
        if (pathVariable.startsWith("{") && pathVariable.endsWith("}")) {
            pathVariable = pathVariable.substring(1, pathVariable.length() - 1);
        }
        String[] parts = pathVariable.split(":");

        return create(parts[0], parts.length > 1 ? parts[1] : null);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getRegexp() {
        return regexp;
    }

    public String getValue() {
        return value;
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return "PathVariable{" + "name='" + name + '\'' + ", pattern='" + regexp + '\'' + ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PathVariable that = (PathVariable) o;
        return Objects.equals(name, that.name) && Objects.equals(regexp, that.regexp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, regexp);
    }
}
