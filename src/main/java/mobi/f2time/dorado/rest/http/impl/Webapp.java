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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.f2time.dorado.exception.DoradoException;
import mobi.f2time.dorado.hotswap.DoradoClassLoader;
import mobi.f2time.dorado.rest.annotation.Controller;
import mobi.f2time.dorado.rest.annotation.HttpMethod;
import mobi.f2time.dorado.rest.annotation.Path;
import mobi.f2time.dorado.rest.controller.RootController;
import mobi.f2time.dorado.rest.http.Filter;
import mobi.f2time.dorado.rest.router.UriRoutingController;
import mobi.f2time.dorado.rest.router.UriRoutingPath;
import mobi.f2time.dorado.rest.router.UriRoutingRegistry;
import mobi.f2time.dorado.rest.server.Dorado;
import mobi.f2time.dorado.rest.util.ClassLoaderUtils;
import mobi.f2time.dorado.rest.util.FileUtils;
import mobi.f2time.dorado.rest.util.PackageScanner;
import mobi.f2time.dorado.rest.util.StringUtils;

/**
 * 
 * @author wangwp
 */
public class Webapp {
	private static final Logger LOG = LoggerFactory.getLogger(Webapp.class);

	private static Webapp webapp;
	private static final String FILTER_URL_PATTERN_ALL = "^/.*";

	private final String[] packages;
	private final boolean reloadable;

	private Webapp(String[] packages, boolean reloadable) {
		this.packages = packages;
		this.reloadable = reloadable;
	}

	public static synchronized void create(String[] packages) {
		webapp = new Webapp(packages, false);
		webapp.initialize();
	}

	public static synchronized void create(String[] packages, boolean reloadable) {
		webapp = new Webapp(packages, reloadable);
		webapp.initialize();
	}

	public static Webapp get() {
		if (webapp == null) {
			throw new IllegalStateException("webapp not initialized, please create it first");
		}
		return webapp;
	}

	public synchronized void reload() {
		if (!reloadable) {
			return;
		}
		Thread.currentThread().setContextClassLoader(Dorado.classLoader);
		webapp.destroy();
		webapp.initialize();
	}

	private void destroy() {
		UriRoutingRegistry.getInstance().clear();
	}

	public void initialize() {
		if (reloadable) {
			watching();
		}

		List<Class<?>> classes = new ArrayList<>();
		try {
			if (packages == null) {
				classes.addAll(PackageScanner.scanClassesWithClasspath(ClassLoaderUtils.getPath(StringUtils.EMPTY)));
			} else {
				for (String scanPackage : packages) {
					classes.addAll(PackageScanner.scan(scanPackage));
				}
			}
			initializeUriRouting(RootController.class);
			classes.forEach(clazz -> {
				initializeUriRouting(clazz);
				initializeWebFilters(clazz);
			});
			UriRoutingRegistry registry = getUriRoutingRegistry();
			if (registry.uriRoutings().isEmpty()) {
				LOG.warn("No Controller are registered, please check first");
			}
		} catch (Exception ex) {
			throw new DoradoException(ex);
		}
	};

	private void watching() {
		new Thread(() -> {
			try {
				reloadWebappIfNeed(ClassLoaderUtils.getPath(StringUtils.EMPTY));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}).start();
		;
	}

	private void reloadWebappIfNeed(String classpath) throws Exception {
		WatchService classFilesWatcher = FileSystems.getDefault().newWatchService();
		java.nio.file.Path rootPath = Paths.get(classpath);

		List<java.nio.file.Path> allWatchDirs = FileUtils.recurseListDirs(rootPath);
		for (java.nio.file.Path watchDir : allWatchDirs) {
			watchDir.register(classFilesWatcher, StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
		}

		while (!Thread.currentThread().isInterrupted()) {
			try {
				WatchKey watchKey = classFilesWatcher.poll(10, TimeUnit.MILLISECONDS);
				if (watchKey == null)
					continue;

				final AtomicBoolean isNeedReload = new AtomicBoolean(false);

				List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
				watchEvents.stream().forEach(event -> {
					java.nio.file.Path watchedPath = (java.nio.file.Path) event.context();
					try {
						LOG.info("File {} changed in classpath, need reload webapp", watchedPath.toString());
						isNeedReload.compareAndSet(false, true);
					} catch (Exception ex) {
						LOG.error("watching file changed error", ex);
					}
				});

				watchKey.reset();
				if (isNeedReload.get()) {
					Dorado.classLoader = new DoradoClassLoader();
					reload();
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

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
