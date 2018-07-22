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
		converters.put(Integer.class, TypeConverter.STRING_INT);

		converters.put(long.class, TypeConverter.STRING_LONG);
		converters.put(Long.class, TypeConverter.STRING_LONG);

		converters.put(double.class, TypeConverter.STRING_DOUBLE);
		converters.put(Double.class, TypeConverter.STRING_DOUBLE);

		converters.put(float.class, TypeConverter.STRING_FLOAT);
		converters.put(Float.class, TypeConverter.STRING_FLOAT);

		converters.put(short.class, TypeConverter.STRING_SHORT);
		converters.put(Short.class, TypeConverter.STRING_SHORT);
	}

	@SuppressWarnings("rawtypes")
	public static TypeConverter resolveConverter(Class<?> type) {
		TypeConverter converter = converters.get(type);
		return converter == null ? TypeConverter.DUMMY : converter;
	}
}
