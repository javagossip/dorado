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
import mobi.f2time.dorado.rest.util.StringUtils;

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

	public void register(Class<?> type) {
		Controller controller = type.getAnnotation(Controller.class);
		if (controller == null)
			return;

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
		for (UriRouting uriRouting : uriRoutingRegistry) {
			routingMethod = uriRouting.path.httpMethod();
			matchResult = uriRouting.path.routingPathPattern().matcher(request.getRequestURI());

			if (matchResult.matches() && (routingMethod == null || (request.getMethod().equals(routingMethod)))) {
				return Router.create(uriRouting.controller, matchResult, request.getMethod());
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

	public void clear() {
		uriRoutingRegistry.clear();
	}
}
