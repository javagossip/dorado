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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import mobi.f2time.dorado.rest.annotation.Controller;
import mobi.f2time.dorado.rest.annotation.HttpMethod;
import mobi.f2time.dorado.rest.annotation.Path;
import mobi.f2time.dorado.rest.http.HttpRequest;
import mobi.f2time.dorado.rest.http.impl.FilterManager;
import mobi.f2time.dorado.rest.util.SimpleLRUCache;
import mobi.f2time.dorado.rest.util.StringUtils;

/**
 * 
 * @author wangwp
 */
public class UriRoutingRegistry {
	private static final UriRoutingRegistry _instance = new UriRoutingRegistry();

	private List<UriRouting> uriRoutingRegistry = new ArrayList<>();
	private final SimpleLRUCache<RoutingCacheKey, Router> cache = SimpleLRUCache.create(512);

	private UriRoutingRegistry() {
	}

	public static UriRoutingRegistry getInstance() {
		return _instance;
	}

	public void register(Class<?> type) {
		Controller controller = type.getAnnotation(Controller.class);
		if (controller == null) {
			return;
		}

		Path classLevelPath = type.getAnnotation(Path.class);
		String controllerPath = classLevelPath == null ? StringUtils.EMPTY : classLevelPath.value();

		Method[] controllerMethods = type.getDeclaredMethods();
		for (Method method : controllerMethods) {
			if (Modifier.isStatic(method.getModifiers()) || method.getAnnotations().length == 0
					|| !Modifier.isPublic(method.getModifiers())) {
				continue;
			}

			Path methodLevelPath = method.getAnnotation(Path.class);
			HttpMethod httpMethod = getHttpMethod(method.getAnnotations());
			String methodPath = methodLevelPath == null ? StringUtils.EMPTY : methodLevelPath.value();

			UriRoutingPath uriRoutingPath = UriRoutingPath.create(String.format("%s%s", controllerPath, methodPath),
					httpMethod);
			UriRoutingController routeController = UriRoutingController.create(uriRoutingPath, type, method);
			register(uriRoutingPath, routeController);
		}
	}

	private HttpMethod getHttpMethod(Annotation[] annotations) {
		HttpMethod httpMethod = null;

		for (Annotation annotation : annotations) {
			httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
			if (httpMethod != null) {
				return httpMethod;
			}
		}
		return null;
	}

	public void register(UriRoutingPath routeMapping, UriRoutingController controller) {
		uriRoutingRegistry.add(UriRouting.create(routeMapping, controller));
		uriRoutingRegistry.sort((a, b) -> a.path.compareTo(b.path));
	}

	public Router findRouteController(HttpRequest request) {
		Matcher matchResult = null;
		String routingMethod = null;

		RoutingCacheKey key = new RoutingCacheKey(request.getRequestURI(), request.getMethod());
		Router router = cache.get(key);

		if (router != null) {
			return router;
		}

		for (UriRouting uriRouting : uriRoutingRegistry) {
			routingMethod = uriRouting.path.httpMethod();
			matchResult = uriRouting.path.routingPathPattern().matcher(request.getRequestURI());

			if (matchResult.matches() && matchMethod(routingMethod, request.getMethod())) {
				router = Router.create(uriRouting.controller, matchResult, request.getMethod());
				router.addFilters(FilterManager.getInstance().match(request.getRequestURI()));
				cache.put(key, router);
				return router;
			}
		}
		return null;
	}

	private boolean matchMethod(String routingMethod, String method) {
		return routingMethod == null || method.equals(routingMethod);
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

	public void clear() {
		uriRoutingRegistry.clear();
	}

	private class RoutingCacheKey {
		private String uri;
		private String method;

		public RoutingCacheKey(String uri, String method) {
			this.uri = uri;
			this.method = method;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((method == null) ? 0 : method.hashCode());
			result = prime * result + ((uri == null) ? 0 : uri.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			RoutingCacheKey other = (RoutingCacheKey) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (method == null) {
				if (other.method != null) {
					return false;
				}
			} else if (!method.equals(other.method)) {
				return false;
			}
			if (uri == null) {
				if (other.uri != null) {
					return false;
				}
			} else if (!uri.equals(other.uri)) {
				return false;
			}
			return true;
		}

		private UriRoutingRegistry getOuterType() {
			return UriRoutingRegistry.this;
		}
	}
}
