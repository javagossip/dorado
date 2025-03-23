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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author wangwp
 */
public final class StringUtils {

    public static final String EMPTY = "";
    public static final String REGEX_SPECIAL_CHARS = ".*+?^$[](){}|\\";
    public static final Pattern REGEXP_PATTEN =
            Pattern.compile("[" + REGEX_SPECIAL_CHARS.replaceAll("(.)", "\\\\$1") + "]");

    private StringUtils() {
    }

    public static String repeat(String str, int count) {
        if (count <= 0) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(str);
        }
        return result.toString();
    }

    public static String defaultString(String str, String defaultStr) {
        if (isBlank(str)) {
            return defaultStr;
        }
        return str;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    public static boolean isBlank(String str) {
        if (str == null || str.trim().isEmpty()) {
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
            return Boolean.parseBoolean(str);
        } catch (Exception ex) {
            //ignored this exception
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

    public static String[] tokenizeToStringArray(String str,
            String delimiters,
            boolean trimTokens,
            boolean ignoreEmptyTokens) {

        if (str == null) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || !token.isEmpty()) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    public static String[] toStringArray(Collection<String> collection) {
        if (collection == null) {
            return null;
        }
        return collection.toArray(new String[0]);
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

    public static boolean isRegExp(String str) {
        //先判断是否包含特殊的正则字符，然后在编译正则表达式确认是否是合法的正则表达式
        if (isBlank(str)) {
            return false;
        }
        boolean matches = REGEXP_PATTEN.matcher(str).find();
        try {
            Pattern.compile(str, Pattern.CASE_INSENSITIVE);
            return matches;
        } catch (Exception ex) {
            //ignore this exception
        }
        return false;
    }

    public static String[] splitTrim(String str, String delimiters) {
        Assert.notNull(str, "str must not be null");
        Assert.notNull(delimiters, "delimiter must not be null");

        String[] splits = Arrays.stream(str.split(delimiters))
                .filter(s -> !isBlank(s))
                .collect(Collectors.toList())
                .toArray(new String[0]);
        return splits;
    }
}
