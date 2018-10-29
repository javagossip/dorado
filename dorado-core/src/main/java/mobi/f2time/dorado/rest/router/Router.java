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
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import mobi.f2time.dorado.exception.DoradoException;
import mobi.f2time.dorado.rest.http.Filter;
import mobi.f2time.dorado.rest.http.HttpRequest;
import mobi.f2time.dorado.rest.http.HttpResponse;

/**
 * 
 * @author wangwp
 */
public class Router {
	private final UriRoutingController controller;
	private final String[] pathVariables;
	private final String httpMethod;
	private final List<Filter> filters;

	private Router(UriRoutingController controller, MatchResult matchResult, String httpMethod) {
		this.filters = new ArrayList<>();
		this.httpMethod = httpMethod;
		this.controller = controller;
		pathVariables = new String[matchResult.groupCount()];

		for (int i = 0; i < pathVariables.length; i++) {
			pathVariables[i] = matchResult.group(i + 1);
		}
	}

	public static Router create(UriRoutingController controller, Matcher matchResult, String httpMethod) {
		return new Router(controller, matchResult, httpMethod);
	}

	public UriRoutingController controller() {
		return this.controller;
	}

	public void addFilters(List<Filter> filters) {
		this.filters.addAll(filters);
	}

	public Object invoke(HttpRequest request, HttpResponse response) {
		try {
			for (Filter filter : filters) {
				if (!filter.preFilter(request, response)) {
					return null;
				}
			}
			return this.controller.invoke(request, response, pathVariables);
		} catch (Exception ex) {
			throw new DoradoException(ex);
		} finally {
			for (Filter filter : filters) {
				filter.postFilter(request, response);
			}
		}
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public String pathVariable(int index) {
		if (index <= 0 || index > pathVariables.length) {
			throw new IllegalArgumentException("");
		}
		return pathVariables[index - 1];
	}

	public String[] pathVariables() {
		return this.pathVariables;
	}
}
