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

import java.lang.reflect.Method;

import ai.houyi.dorado.exception.DoradoException;

/**
 * @author weiping wang
 *
 */
public class ExceptionHandler {
	private final Method exceptionHandleMethod;
	private final Class<?> exceptionAdvicorType;

	private ExceptionHandler(Class<?> type, Method exceptionHandleMethod) {
		this.exceptionAdvicorType = type;
		this.exceptionHandleMethod = exceptionHandleMethod;

		if (!exceptionHandleMethod.isAccessible()) {
			exceptionHandleMethod.setAccessible(true);
		}
	}

	public void handleException(Throwable throwable) {
		try {
			exceptionHandleMethod.invoke(exceptionAdvicorType, throwable);
		} catch (Throwable ex) {
			throw new DoradoException(ex);
		}
	}
}
