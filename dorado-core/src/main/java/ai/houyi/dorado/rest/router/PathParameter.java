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

import java.util.Objects;

public class PathParameter {

    private final String name;
    private final String pattern;
    private String value;

    public PathParameter(String name, String pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    public static PathParameter create(String name, String pattern) {
        return new PathParameter(name, pattern);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PathVariable{" + "name='" + name + '\'' + ", pattern='" + pattern + '\'' + ", value='" + value + '\'' +
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
        PathParameter that = (PathParameter) o;
        return Objects.equals(name, that.name) && Objects.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pattern);
    }
}
