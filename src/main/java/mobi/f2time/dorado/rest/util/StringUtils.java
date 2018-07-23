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

/**
 * 
 * @author wangwp
 */
public final class StringUtils {
	public static final String EMPTY = "";

	public static String defaultString(String str, String defaultStr) {
		if (isBlank(str))
			return defaultStr;
		return str;
	}

	public static boolean isBlank(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static int toInt(String str) {
		if (isBlank(str)) {
			return 0;
		}
		try {
			return Integer.parseInt(str);
		} catch (Exception ex) {
			// do nothing
		}
		return 0;
	}

	public static long toLong(String str) {
		if (isBlank(str)) {
			return 0L;
		}
		try {
			return Long.parseLong(str);
		} catch (Exception ex) {
			// do nothing
		}
		return 0L;
	}

	public static float toFloat(String str) {
		if (isBlank(str)) {
			return 0.0F;
		}
		try {
			return Float.parseFloat(str);
		} catch (Exception ex) {
			// do nothing
		}
		return 0.0F;
	}

	public static double toDouble(String str) {
		if (isBlank(str)) {
			return 0.0D;
		}
		try {
			return Double.parseDouble(str);
		} catch (Exception ex) {
		}
		return 0.0D;
	}

	public static boolean toBoolean(String str) {
		if (isBlank(str)) {
			return false;
		}
		try {
			return Boolean.valueOf(str);
		} catch (Exception ex) {
		}
		return false;
	}

	public static short toShort(String str) {
		if (isBlank(str)) {
			return 0;
		}
		try {
			return Short.parseShort(str);
		} catch (Exception ex) {
		}
		return 0;
	}

	public static char toChar(String str) {
		if (isBlank(str)) {
			return (char) 0;
		}

		char[] chars = str.toCharArray();
		if (chars.length > 1) {
			throw new IllegalArgumentException("invalid char");
		}
		return chars[0];
	}
}
