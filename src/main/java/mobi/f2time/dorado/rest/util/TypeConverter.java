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
package mobi.f2time.dorado.rest.util;

import java.io.InputStream;

/**
 * 
 * @author wangwp
 */
public interface TypeConverter<F, T> {
	T convert(F value);

	TypeConverter<String, Integer> STRING_INT = s -> s == null ? null : Integer.parseInt(s);

	TypeConverter<String, Long> STRING_LONG = s -> s == null ? null : Long.parseLong(s);

	TypeConverter<String, Float> STRING_FLOAT = s -> s == null ? null : Float.parseFloat(s);

	TypeConverter<String, Double> STRING_DOUBLE = s -> s == null ? null : Double.parseDouble(s);

	TypeConverter<String, Short> STRING_SHORT = s -> s == null ? null : Short.parseShort(s);

	TypeConverter<String, Boolean> STRING_BOOL = s -> s == null ? null : Boolean.valueOf(s);

	TypeConverter<String, Character> STRING_CHAR = s -> s == null ? null : Character.valueOf(s.toCharArray()[0]);

	TypeConverter<String, Byte> STRING_BYTE = s -> s == null ? null : Byte.valueOf(s);

	TypeConverter<Object, Object> DUMMY = s -> s;

	TypeConverter<InputStream, InputStream> INPUTSTREAM = s -> s;

	TypeConverter<InputStream, String> STRING_UTF8 = s -> IOUtils.toString(s, "UTF-8");
}
