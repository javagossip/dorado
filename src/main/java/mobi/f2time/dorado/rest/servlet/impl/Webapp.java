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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.f2time.dorado.rest.annotation.Controller;
import mobi.f2time.dorado.rest.annotation.HttpMethod;
import mobi.f2time.dorado.rest.annotation.Path;
import mobi.f2time.dorado.rest.router.UriRoutingController;
import mobi.f2time.dorado.rest.router.UriRoutingPath;
import mobi.f2time.dorado.rest.router.UriRoutingRegistry;
import mobi.f2time.dorado.rest.util.Constant;
import mobi.f2time.dorado.rest.util.PackageScanner;

/**
 * 
 * @author wangwp
 */
public class Webapp {
	private static final Logger LOG = LoggerFactory.getLogger(Webapp.class);

	private static Webapp webapp;

	private final String packageName;

	private Webapp(String packageName) {
		this.packageName = packageName;
	}

	public static synchronized void create(String packageName) {
		webapp = new Webapp(packageName);
		webapp.initialize();
	}

	public static Webapp get() {
		if (webapp == null) {
			throw new IllegalStateException("webapp not initialized, please create it first");
		}
		return webapp;
	}

	public void initialize() {

		try {
			List<Class<?>> classes = PackageScanner.scan(packageName);
			classes.forEach(clazz -> initializeUriRouting(clazz));
		} catch (Exception ex) {
			LOG.error("");
			throw new RuntimeException(ex);
		}
	}

	private void initializeUriRouting(Class<?> clazz) {
		initUriRoutingController(clazz);
	}
	
	private void initUriRoutingController(Class<?> c) {
		Controller controller = c.getAnnotation(Controller.class);
		if (controller == null)
			return;

		Path classLevelPath = c.getAnnotation(Path.class);
		String controllerPath = classLevelPath == null ? Constant.BLANK_STRING : classLevelPath.value();

		Method[] controllerMethods = c.getMethods();
		for (Method controllerMethod : controllerMethods) {
			if (Modifier.isStatic(controllerMethod.getModifiers()) || controllerMethod.getAnnotations().length == 0) {
				continue;
			}

			Path methodLevelPath = controllerMethod.getAnnotation(Path.class);
			HttpMethod httpMethod = controllerMethod.getAnnotation(HttpMethod.class);

			String methodPath = methodLevelPath == null ? Constant.BLANK_STRING : methodLevelPath.value();

			UriRoutingPath uriRoutingPath = UriRoutingPath.create(String.format("%s%s", controllerPath, methodPath),
					httpMethod);
			UriRoutingController routeController = UriRoutingController.create(uriRoutingPath, c, controllerMethod);
			getUriRoutingRegistry().register(uriRoutingPath, routeController);
		}
	}

	public FilterManager getFilterManager() {
		return FilterManager.getInstance();
	}

	public UriRoutingRegistry getUriRoutingRegistry() {
		return UriRoutingRegistry.getInstance();
	}
}
