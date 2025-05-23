/*
 * Copyright 2017 The OpenDSP Project
 *
 * The OpenDSP Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package ai.houyi.dorado.rest.util;

import java.io.InputStream;
import java.lang.reflect.Type;

import ai.houyi.dorado.rest.ObjectSerializer;

/**
 * @author wangwp
 */
public class SerializeUtils {

    public static byte[] serialize(Object t) {
        return ObjectSerializer.DEFAULT.serialize(t);
    }

    public static Object deserialize(InputStream in, Type parameterizedType) {
        return ObjectSerializer.DEFAULT.deserialize(in, parameterizedType);
    }
}
