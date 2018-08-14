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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author wangwp
 */
public class TypeConverters {
	private static Map<Class<?>, TypeConverter<?, ?>> converters = new HashMap<>();

	static {
		converters.put(int.class, TypeConverter.STRING_INT);
		converters.put(long.class, TypeConverter.STRING_LONG);
		converters.put(double.class, TypeConverter.STRING_DOUBLE);
		converters.put(float.class, TypeConverter.STRING_FLOAT);
		converters.put(short.class, TypeConverter.STRING_SHORT);
		converters.put(boolean.class, TypeConverter.STRING_BOOL);
		converters.put(char.class, TypeConverter.STRING_CHAR);
		converters.put(byte.class, TypeConverter.STRING_BYTE);

		converters.put(Float.class, TypeConverter.STRING_FLOAT_WRAPPER);
		converters.put(Integer.class, TypeConverter.STRING_INT_WRAPPER);
		converters.put(Long.class, TypeConverter.STRING_LONG_WRAPPER);
		converters.put(Double.class, TypeConverter.STRING_DOUBLE_WRAPPER);
		converters.put(Short.class, TypeConverter.STRING_SHORT_WRAPPER);
		converters.put(Boolean.class, TypeConverter.STRING_BOOL_WRAPPER);
		converters.put(Character.class, TypeConverter.STRING_CHAR_WRAPPER);
		converters.put(Byte.class, TypeConverter.STRING_BYTE_WRAPPER);
	}

	@SuppressWarnings("rawtypes")
	public static TypeConverter resolveConverter(Class<?> type) {
		TypeConverter converter = converters.get(type);
		return converter == null ? TypeConverter.DUMMY : converter;
	}
}
