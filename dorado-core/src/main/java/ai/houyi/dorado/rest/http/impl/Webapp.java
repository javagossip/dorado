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
package ai.houyi.dorado.rest.http.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ai.houyi.dorado.Dorado;
import ai.houyi.dorado.exception.DoradoException;
import ai.houyi.dorado.rest.ResourceRegister;
import ai.houyi.dorado.rest.ResourceRegisters;
import ai.houyi.dorado.rest.annotation.ExceptionAdvice;
import ai.houyi.dorado.rest.annotation.FilterPath;
import ai.houyi.dorado.rest.controller.RootController;
import ai.houyi.dorado.rest.http.Filter;
import ai.houyi.dorado.rest.http.MethodReturnValueHandler;
import ai.houyi.dorado.rest.router.UriRoutingRegistry;
import ai.houyi.dorado.rest.util.LogUtils;
import ai.houyi.dorado.rest.util.PackageScanner;

/**
 * 
 * @author wangwp
 */
public class Webapp {
	private static Webapp webapp;

	private final String[] packages;
	private MethodReturnValueHandler methodReturnValueHandler;

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

	public MethodReturnValueHandler getMethodReturnValueHandler() {
		return this.methodReturnValueHandler;
	}

	public void initialize() {
		List<Class<?>> classes = new ArrayList<>();

		for (ResourceRegister resourceRegister : ResourceRegisters.getInstance().getResourceRegisters()) {
			resourceRegister.register();
		}

		try {
			for (String scanPackage : packages) {
				classes.addAll(PackageScanner.scan(scanPackage));
			}

			initializeUriRouting(RootController.class);
			classes.forEach(clazz -> {
				registerWebComponent(clazz);
			});

			UriRoutingRegistry registry = getUriRoutingRegistry();
			if (registry.uriRoutings().isEmpty()) {
				LogUtils.warn("No Controller are registered, please check first");
			}
		} catch (Exception ex) {
			throw new DoradoException(ex);
		}
	};

	private void registerWebComponent(Class<?> type) {
		Annotation exceptionAdvice = type.getAnnotation(ExceptionAdvice.class);
		if (exceptionAdvice != null) {
			registerExceptionAdvice(type);
		}

		if (MethodReturnValueHandler.class.isAssignableFrom(type)) {
			if (this.methodReturnValueHandler != null) {
				throw new IllegalStateException("Only one instance for [MethodReturnValueHandler] is allowed");
			}
			this.methodReturnValueHandler = (MethodReturnValueHandler) Dorado.beanContainer.getBean(type);
		}

		if (Filter.class.isAssignableFrom(type)) {
			FilterPath filterPath = type.getAnnotation(FilterPath.class);
			if (filterPath == null) {
				return;
			}

			FilterConfiguration filterConfiguration = FilterConfiguration.builder()
					.withPathPatterns(Arrays.asList(filterPath.include()))
					.withExcludePathPatterns(Arrays.asList(filterPath.exclude()))
					.withFilter((Filter) Dorado.beanContainer.getBean(type)).build();
			FilterManager.getInstance().addFilterConfiguration(filterConfiguration);
		} else {
			initializeUriRouting(type);
		}
	}

	private void registerExceptionAdvice(Class<?> type) {
		WebComponentRegistry.getWebComponentRegistry().registerExceptionHandlers(type);
	}

	private void initializeUriRouting(Class<?> c) {
		UriRoutingRegistry.getInstance().register(c);
	}

	public UriRoutingRegistry getUriRoutingRegistry() {
		return UriRoutingRegistry.getInstance();
	}
}
