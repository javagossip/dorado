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

import io.netty.util.CharsetUtil;

/**
 * 
 * @author wangwp
 */
public interface TypeConverter<F, T> {
	T convert(F value);

	TypeConverter<String, Integer> STRING_INT = s -> s == null ? null : StringUtils.toInt(s);

	TypeConverter<String, Long> STRING_LONG = s -> s == null ? null : StringUtils.toLong(s);

	TypeConverter<String, Float> STRING_FLOAT = s -> s == null ? null : StringUtils.toFloat(s);

	TypeConverter<String, Double> STRING_DOUBLE = s -> s == null ? null : StringUtils.toDouble(s);

	TypeConverter<String, Short> STRING_SHORT = s -> s == null ? null : StringUtils.toShort(s);

	TypeConverter<String, Boolean> STRING_BOOL = s -> s == null ? null : StringUtils.toBoolean(s);

	TypeConverter<String, Character> STRING_CHAR = s -> {
		throw new UnsupportedOperationException("unsupport convert string to char");
	};

	TypeConverter<String, Byte> STRING_BYTE = s -> {
		throw new UnsupportedOperationException("unsupport convert string to byte");
	};

	TypeConverter<Object, Object> DUMMY = s -> s;

	TypeConverter<InputStream, InputStream> INPUTSTREAM = s -> s;

	TypeConverter<InputStream, String> STRING_UTF8 = s -> IOUtils.toString(s, CharsetUtil.UTF_8.name());

	TypeConverter<String, Float> STRING_FLOAT_WRAPPER = s -> s == null ? null : Float.valueOf(s);

	TypeConverter<String, Integer> STRING_INT_WRAPPER = s -> s == null ? null : Integer.valueOf(s);

	TypeConverter<String, Long> STRING_LONG_WRAPPER = s -> s == null ? null : Long.valueOf(s);

	TypeConverter<String, Double> STRING_DOUBLE_WRAPPER = s -> s == null ? null : Double.valueOf(s);

	TypeConverter<String, Short> STRING_SHORT_WRAPPER = s -> s == null ? null : Short.valueOf(s);

	TypeConverter<String, Boolean> STRING_BOOL_WRAPPER = s -> s == null ? null : Boolean.valueOf(s);

	TypeConverter<String, Character> STRING_CHAR_WRAPPER = s -> {
		throw new UnsupportedOperationException("unsupport convert string to character");
	};

	TypeConverter<String, Byte> STRING_BYTE_WRAPPER = s -> {
		throw new UnsupportedOperationException("unsupport convert string to byte");
	};
}
