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
package mobi.f2time.dorado.rest.http.impl;

import java.util.ArrayList;
import java.util.List;

import mobi.f2time.dorado.Dorado;
import mobi.f2time.dorado.exception.DoradoException;
import mobi.f2time.dorado.rest.annotation.Path;
import mobi.f2time.dorado.rest.controller.RootController;
import mobi.f2time.dorado.rest.http.Filter;
import mobi.f2time.dorado.rest.router.UriRoutingRegistry;
import mobi.f2time.dorado.rest.util.ClassLoaderUtils;
import mobi.f2time.dorado.rest.util.LogUtils;
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

	private Webapp(String[] packages, boolean springOn) {
		this.packages = packages;
	}

	public static synchronized void create(String[] packages) {
		create(packages, false);
	}

	public static synchronized void create(String[] packages, boolean springOn) {
		webapp = new Webapp(packages, springOn);
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
		
		if (Dorado.isEnableSwagger) {
			getUriRoutingRegistry()
					.register(ClassLoaderUtils.loadClass("mobi.f2time.dorado.swagger.controller.SwaggerV2Controller"));
		}

		try {
			for (String scanPackage : packages) {
				classes.addAll(PackageScanner.scan(scanPackage));
			}

			initializeUriRouting(RootController.class);
			classes.forEach(clazz -> {
				initializeUriRouting(clazz);
				initializeWebFilters(clazz);
			});

			UriRoutingRegistry registry = getUriRoutingRegistry();
			if (registry.uriRoutings().isEmpty()) {
				LogUtils.warn("No Controller are registered, please check first");
			}
		} catch (Exception ex) {
			throw new DoradoException(ex);
		}
	};

	private void initializeWebFilters(Class<?> clazz) {
		if (!Filter.class.isAssignableFrom(clazz) || clazz.isInterface()) {
			return;
		}

		Path filterPath = clazz.getAnnotation(Path.class);
		String urlPattern = filterPath == null ? null : filterPath.value();
		urlPattern = StringUtils.defaultString(urlPattern, FILTER_URL_PATTERN_ALL);

		getFilterManager().addFilter(new FilterConfiguration(urlPattern, (Filter) ClassLoaderUtils.newInstance(clazz)));
	}

	private void initializeUriRouting(Class<?> c) {
		UriRoutingRegistry.getInstance().register(c);
	}

	public FilterManager getFilterManager() {
		return FilterManager.getInstance();
	}

	public UriRoutingRegistry getUriRoutingRegistry() {
		return UriRoutingRegistry.getInstance();
	}
}
