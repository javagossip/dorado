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
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import mobi.f2time.dorado.rest.http.HttpRequest;

/**
 * 
 * @author wangwp
 */
public class UriRoutingRegistry {
	private static final UriRoutingRegistry _instance = new UriRoutingRegistry();

	private List<UriRouting> uriRoutingRegistry = new ArrayList<>();

	private UriRoutingRegistry() {
	}

	public static UriRoutingRegistry getInstance() {
		return _instance;
	}

	public void register(UriRoutingPath routeMapping, UriRoutingController controller) {
		uriRoutingRegistry.add(UriRouting.create(routeMapping, controller));
		uriRoutingRegistry.sort((a, b) -> a.path.compareTo(b.path));
	}

	public UriRoutingMatchResult findRouteController(HttpRequest request) {
		Matcher matchResult = null;

		String routingMethod = null;
		for (UriRouting uriRouting : uriRoutingRegistry) {
			routingMethod = uriRouting.path.httpMethod();
			matchResult = uriRouting.path.routingPathPattern().matcher(request.getRequestURI());

			if (matchResult.matches() && (routingMethod == null || (request.getMethod().equals(routingMethod)))) {
				return UriRoutingMatchResult.create(uriRouting.controller, matchResult);
			}
		}
		return null;
	}

	public List<UriRouting> uriRoutings() {
		return Collections.unmodifiableList(uriRoutingRegistry);
	}

	@Override
	public String toString() {
		return "UriRoutingRegistry [uriRouteMappingRegistry=" + uriRoutingRegistry + "]";
	}

	public static class UriRouting {
		private final UriRoutingPath path;
		private final UriRoutingController controller;

		private UriRouting(UriRoutingPath path, UriRoutingController controller) {
			this.path = path;
			this.controller = controller;
		}

		public static UriRouting create(UriRoutingPath path, UriRoutingController controller) {
			return new UriRouting(path, controller);
		}

		public UriRoutingPath uriRoutingPath() {
			return path;
		}

		public UriRoutingController controller() {
			return controller;
		}

		@Override
		public String toString() {
			return "UriRouting [path=" + path + ", controller=" + controller + "]";
		}
	}
}
