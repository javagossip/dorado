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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

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

	public static String[] tokenizeToStringArray(String str, String delimiters) {
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
			boolean ignoreEmptyTokens) {

		if (str == null) {
			return null;
		}

		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	public static String[] toStringArray(Collection<String> collection) {
		if (collection == null) {
			return null;
		}
		return collection.toArray(new String[collection.size()]);
	}

	public static boolean hasText(String str) {
		return (hasLength(str) && containsText(str));
	}

	private static boolean containsText(CharSequence str) {
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasLength(String str) {
		return (str != null && !str.isEmpty());
	}
}
