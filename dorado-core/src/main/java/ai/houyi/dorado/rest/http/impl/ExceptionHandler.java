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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import ai.houyi.dorado.exception.DoradoException;
import ai.houyi.dorado.rest.annotation.Status;
import ai.houyi.dorado.rest.util.MethodDescriptor;

/**
 * @author weiping wang
 *
 */
public class ExceptionHandler {
	private final Method exceptionHandleMethod;
	private final Object exceptionAdvicor;
	private final MethodDescriptor descriptor;

	private ExceptionHandler(Object exceptionAdvicor, Method exceptionHandleMethod) {
		this.exceptionAdvicor = exceptionAdvicor;
		this.exceptionHandleMethod = exceptionHandleMethod;
		this.descriptor = MethodDescriptor.create(exceptionAdvicor.getClass(), exceptionHandleMethod);

		if (!exceptionHandleMethod.isAccessible()) {
			exceptionHandleMethod.setAccessible(true);
		}
	}

	public static ExceptionHandler newExceptionHandler(Object exceptionAdvicor, Method exceptionHandleMethod) {
		return new ExceptionHandler(exceptionAdvicor, exceptionHandleMethod);
	}

	public Status status() {
		Annotation[] annotations = descriptor.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == Status.class)
				return (Status) annotation;
		}
		return null;
	}

	public String produce() {
		return descriptor.produce();
	}

	public Object handleException(Throwable throwable) {
		try {
			return exceptionHandleMethod.invoke(exceptionAdvicor, throwable);
		} catch (Throwable ex) {
			throw new DoradoException(ex);
		}
	}
}
