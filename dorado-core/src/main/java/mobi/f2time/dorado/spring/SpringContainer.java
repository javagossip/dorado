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
package mobi.f2time.dorado.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import mobi.f2time.dorado.BeanContainer;
import mobi.f2time.dorado.Dorado;
import mobi.f2time.dorado.rest.annotation.Controller;
import mobi.f2time.dorado.rest.controller.RootController;
import mobi.f2time.dorado.rest.server.DoradoServerBuilder;

/**
 * 
 * @author wangwp
 */
public final class SpringContainer implements BeanContainer {
	private static SpringContainer instance;

	private final ApplicationContext applicationContext;

	private SpringContainer(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		Dorado.beanContainer = this;
	}

	public synchronized static void create(ApplicationContext applicationContext) {
		DoradoServerBuilder builder = Dorado.serverConfig;
		if (builder == null) {
			throw new IllegalStateException("Please init DoradoServer first!");
		}

		if (!(applicationContext instanceof DoradoApplicationContext)
				&& (applicationContext instanceof BeanDefinitionRegistry)) {
			ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(
					(BeanDefinitionRegistry) applicationContext);
			scanner.resetFilters(false);
			scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
			scanner.scan(builder.scanPackages());
		}

		instance = new SpringContainer(applicationContext);
		Dorado.springInitialized = true;
	}

	public static SpringContainer get() {
		return instance;
	}

	@Override
	public <T> T getBean(Class<T> type) {
		if (type == RootController.class) {
			return BeanContainer.DEFAULT.getBean(type);
		}
		try {
			return applicationContext.getBean(type);
		} catch (Throwable ex) {
			return BeanContainer.DEFAULT.getBean(type);
		}
	}

	public static void create(String[] scanPackages) {
		if (instance != null) {
			throw new IllegalStateException("SpringContainer has been initialized");
		}

		DoradoApplicationContext applicationContext = new DoradoApplicationContext(scanPackages);
		create(applicationContext);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> applicationContext.close()));
	}
}
