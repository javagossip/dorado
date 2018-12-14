/*
 * Copyright 2017 The OpenAds Project
 *
 * The OpenAds Project licenses this file to you under the Apache License,
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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author weiping wang
 *
 */
public final class WebComponentRegistry {
	private static final WebComponentRegistry registry=new WebComponentRegistry();
	
	private final ConcurrentMap<Class<? extends Throwable>, ExceptionHandler> exceptionHandlerRegistry = new ConcurrentHashMap<>();
	
	private WebComponentRegistry() {}
	
	public ExceptionHandler getExceptionHandler(Class<? extends Throwable> exceptionType) {
		return registry.getExceptionHandler(exceptionType);
	}
}
