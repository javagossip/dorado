/*
 * Copyright 2017 The OpenAds Project
 *
 * The OpenAds Project licenses this file to you under the Apache License,
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
package ai.houyi.dorado.rest.http;

import java.util.ArrayList;
import java.util.List;

import ai.houyi.dorado.rest.util.PathMatcher;
import ai.houyi.dorado.rest.util.PathMatchers;

/**
 * @author weiping wang
 *
 */
public class MethodReturnValueHandlerConfig {
	private static final PathMatcher pathMatcher = PathMatchers.getPathMatcher();

	private MethodReturnValueHandler returnValueHandler;
	private final List<String> excludePaths;

	public MethodReturnValueHandlerConfig(MethodReturnValueHandler handler) {
		this.excludePaths = new ArrayList<>();

		this.excludePaths.add("/");
		this.excludePaths.add("/status");
		this.excludePaths.add("/config");
		this.excludePaths.add("/services");
		this.excludePaths.add("/swagger*");
		this.excludePaths.add("/api-docs/*");
		this.excludePaths.add("/api-docs*");

		this.returnValueHandler = handler;
	}

	public void addExcludePath(String path) {
		this.excludePaths.add(path);
	}

	public boolean exclude(String url) {
		for (String excludePath : excludePaths) {
			if (pathMatcher.match(excludePath, url)) {
				return true;
			}
		}
		return false;
	}

	public MethodReturnValueHandler getHandler() {
		return returnValueHandler;
	}
}
