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
package mobi.f2time.dorado.rest.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.f2time.dorado.rest.annotation.HttpMethod;

/**
 * 
 * @author wangwp
 */
public class UriRoutingPath implements Comparable<UriRoutingPath> {
	private static final Pattern pathVariablePattern = Pattern.compile("\\{([^{}]*)\\}");
	private static final String defaultPathVariableRegex = "[^/]+";

	private String routingPath;
	private Pattern routingPathPattern;
	private HttpMethod httpMethod;

	private Map<String, Integer> pathVariableIndexHolder = new HashMap<>();

	private UriRoutingPath(String routingPath, HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
		this.routingPath = routingPath;

		Matcher matchResult = pathVariablePattern.matcher(routingPath);

		int pathVariableIndex = 0;
		StringBuffer routingPathRegex = new StringBuffer();

		while (matchResult.find()) {
			String pathVariableExpression = matchResult.group(1);
			String[] pathVariableSegments = pathVariableExpression.split(":");

			String pathVariableRegex = pathVariableSegments.length == 2 ? pathVariableSegments[1]
					: defaultPathVariableRegex;
			String pathVariableName = pathVariableSegments[0];

			matchResult.appendReplacement(routingPathRegex, String.format("(%s)", pathVariableRegex));
			pathVariableIndexHolder.put(pathVariableName, pathVariableIndex);
			pathVariableIndex++;
		}
		matchResult.appendTail(routingPathRegex);

		routingPathPattern = Pattern.compile(routingPathRegex.toString());
	}

	public static UriRoutingPath create(String uri, HttpMethod httpMethod) {
		return new UriRoutingPath(uri, httpMethod);
	}

	public Pattern routingPathPattern() {
		return this.routingPathPattern;
	}

	public int resolvePathIndex(String parameterName) {
		Integer pathIndex = pathVariableIndexHolder.get(parameterName);
		return pathIndex == null ? -1 : pathIndex;
	}

	public String routingPath() {
		return this.routingPath;
	}

	public String httpMethod() {
		return httpMethod == null ? null : httpMethod.value();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 1;
		hash = prime * hash + ((httpMethod == null) ? 0 : httpMethod.hashCode());
		hash = prime * hash + ((routingPath == null) ? 0 : routingPath.hashCode());
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UriRoutingPath other = (UriRoutingPath) obj;
		if (httpMethod == null) {
			if (other.httpMethod != null)
				return false;
		} else if (!httpMethod.equals(other.httpMethod))
			return false;
		if (routingPath == null) {
			if (other.routingPath != null)
				return false;
		} else if (!routingPath.equals(other.routingPath))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UriRoutingPath [routingPath=" + routingPath + ", routingPathPattern=" + routingPathPattern
				+ ", httpMethod=" + httpMethod + ", pathVariableIndexHolder=" + pathVariableIndexHolder + "]";
	}

	@Override
	public int compareTo(UriRoutingPath o) {
		int targetPathSegments = o.routingPath.split("/").length;
		int thisPathSegments = routingPath.split("/").length;

		if (targetPathSegments != thisPathSegments) {
			return targetPathSegments - thisPathSegments;
		}

		return pathVariableIndexHolder.size() - o.pathVariableIndexHolder.size();
	}
}
