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
package mobi.f2time.dorado.rest.servlet.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import mobi.f2time.dorado.exception.DoradoException;
import mobi.f2time.dorado.rest.annotation.Controller;
import mobi.f2time.dorado.rest.annotation.HttpMethod;
import mobi.f2time.dorado.rest.annotation.Path;
import mobi.f2time.dorado.rest.controller.RootController;
import mobi.f2time.dorado.rest.router.UriRoutingController;
import mobi.f2time.dorado.rest.router.UriRoutingPath;
import mobi.f2time.dorado.rest.router.UriRoutingRegistry;
import mobi.f2time.dorado.rest.servlet.Filter;
import mobi.f2time.dorado.rest.util.ClassLoaderUtils;
import mobi.f2time.dorado.rest.util.PackageScanner;
import mobi.f2time.dorado.rest.util.StringUtils;

/**
 * 
 * @author wangwp
 */
public class Webapp {
	private static Webapp webapp;
	private static final String FILTER_URL_PATTERN_ALL = "^/.*";

	private final String[] packages;

	private Webapp(String[] packages) {
		this.packages = packages;
	}

	public static synchronized void create(String[] packages) {
		webapp = new Webapp(packages);
		webapp.initialize();
	}

	public static Webapp get() {
		if (webapp == null) {
			throw new IllegalStateException("webapp not initialized, please create it first");
		}
		return webapp;
	}

	public void initialize() {
		List<Class<?>> classes = new ArrayList<>();
		try {
			for (String scanPackage : packages) {
				classes.addAll(PackageScanner.scan(scanPackage));
			}
			
			initializeUriRouting(RootController.class);

			classes.forEach(clazz -> {
				initializeUriRouting(clazz);
				initializeWebFilters(clazz);
			});
		} catch (Exception ex) {
			throw new DoradoException(ex);
		}
	};

	private void initializeWebFilters(Class<?> clazz) {
		if (!Filter.class.isAssignableFrom(clazz)) {
			return;
		}

		Path filterPath = clazz.getAnnotation(Path.class);
		String urlPattern = filterPath == null ? null : filterPath.value();
		urlPattern = StringUtils.defaultString(urlPattern, FILTER_URL_PATTERN_ALL);

		getFilterManager().addFilter(new FilterConfiguration(urlPattern, (Filter) ClassLoaderUtils.newInstance(clazz)));
	}

	private void initializeUriRouting(Class<?> c) {
		Controller controller = c.getAnnotation(Controller.class);
		if (controller == null)
			return;

		Path classLevelPath = c.getAnnotation(Path.class);
		String controllerPath = classLevelPath == null ? StringUtils.EMPTY : classLevelPath.value();

		Method[] controllerMethods = c.getDeclaredMethods();
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
			UriRoutingController routeController = UriRoutingController.create(uriRoutingPath, c, method);
			getUriRoutingRegistry().register(uriRoutingPath, routeController);
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

	public FilterManager getFilterManager() {
		return FilterManager.getInstance();
	}

	public UriRoutingRegistry getUriRoutingRegistry() {
		return UriRoutingRegistry.getInstance();
	}
}
