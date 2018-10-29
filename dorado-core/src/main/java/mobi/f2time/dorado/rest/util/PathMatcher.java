/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.f2time.dorado.rest.util;

import java.util.Comparator;
import java.util.Map;

/**
 * Strategy interface for {@code String}-based path matching.
 *
 * The default implementation is {@link AntPathMatcher}, supporting the
 * Ant-style pattern syntax.
 *
 * @author Juergen Hoeller
 * @since 1.2
 * @see AntPathMatcher
 */
public interface PathMatcher {

	boolean isPattern(String path);

	boolean match(String pattern, String path);

	boolean matchStart(String pattern, String path);

	String extractPathWithinPattern(String pattern, String path);

	Map<String, String> extractUriTemplateVariables(String pattern, String path);

	Comparator<String> getPatternComparator(String path);

	String combine(String pattern1, String pattern2);
}
