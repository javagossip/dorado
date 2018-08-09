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

import org.springframework.context.ApplicationContext;

/**
 * 
 * @author wangwp
 */
public final class SpringContainer {
	private static SpringContainer instance;

	private final ApplicationContext applicationContext;

	private SpringContainer(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized static void create(ApplicationContext applicationContext) {
		instance = new SpringContainer(applicationContext);
	}

	public static SpringContainer get() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(String name) {
		return (T) applicationContext.getBean(name);
	}

	public <T> T getBean(Class<T> type) {
		return applicationContext.getBean(type);
	}
}
