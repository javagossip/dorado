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
package mobi.f2time.dorado.rest.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import mobi.f2time.dorado.rest.DefaultParameterNameResolver;
import mobi.f2time.dorado.rest.ParameterNameResolver;
import mobi.f2time.dorado.rest.annotation.Consume;
import mobi.f2time.dorado.rest.annotation.Produce;
import mobi.f2time.dorado.rest.servlet.HttpRequest;
import mobi.f2time.dorado.rest.servlet.HttpResponse;

/**
 * 
 * @author wangwp
 */
public class MethodDescriptor {
	private final Class<?> clazz;
	private final Method method;

	private Annotation[] annotations;
	private Object invokeTarget;
	private Class<?> returnType;

	private MethodParameter[] methodParameters;
	private Consume consume;
	private Produce produce;

	private MethodDescriptor(Class<?> clazz, Method method) {
		this(clazz, method, new DefaultParameterNameResolver());
	}

	private MethodDescriptor(Class<?> clazz, Method method, ParameterNameResolver parameterNameResolver) {
		this.clazz = clazz;
		this.method = method;
		this.returnType = method.getReturnType();

		Class<?>[] parameterTypes = method.getParameterTypes();
		String[] parameterNames = parameterNameResolver.getParameterNames(method);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		this.invokeTarget = ClassLoaderUtils.newInstance(clazz);
		this.annotations = method.getAnnotations();
		this.consume = method.getAnnotation(Consume.class);
		this.produce = method.getAnnotation(Produce.class);

		methodParameters = new MethodParameter[method.getParameterCount()];
		for (int i = 0; i < method.getParameterCount(); i++) {
			Annotation annotation = parameterAnnotations[i].length == 0 ? null : parameterAnnotations[i][0];
			Class<?> type = parameterTypes[i];
			String name = parameterNames[i];

			methodParameters[i] = MethodParameter.create(name, type, annotation);
		}
	}

	public static MethodDescriptor create(Class<?> clazz, Method method) {
		MethodDescriptor methodDescriptor = new MethodDescriptor(clazz, method);
		return methodDescriptor;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public Method getMethod() {
		return method;
	}

	public MethodParameter[] getParameters() {
		return this.methodParameters;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public Object getInvokeTarget() {
		return invokeTarget;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public String consume() {
		return consume == null ? "*/*" : consume.value();
	}

	public String produce() {
		return produce == null ? "*/*" : produce.value();
	}

	public static class MethodParameter {
		private String name;
		private Class<?> type;
		private Annotation annotation;
		private Class<?> annotationType;

		private MethodParameter(String name, Class<?> type, Annotation annotation) {
			this.name = name;
			this.type = type;
			this.annotation = annotation;
			this.annotationType = annotation == null ? null : annotation.annotationType();
			if (type == HttpRequest.class)
				this.annotationType = HttpRequest.class;
			if (type == HttpResponse.class)
				this.annotationType = HttpResponse.class;
		}

		public static MethodParameter create(String name, Class<?> type, Annotation annotation) {
			return new MethodParameter(name, type, annotation);
		}

		public String getName() {
			return this.name;
		}

		public Class<?> getType() {
			return this.type;
		}

		public Annotation getAnnotation() {
			return this.annotation;
		}

		public Class<?> getAnnotationType() {
			return this.annotationType;
		}

		@Override
		public String toString() {
			return "MethodParameter [name=" + name + ", type=" + type + ", annotation=" + annotation
					+ ", annotationType=" + annotationType + "]";
		}
	}
}
