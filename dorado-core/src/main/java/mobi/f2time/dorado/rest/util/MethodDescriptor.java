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

import static mobi.f2time.dorado.rest.util.ProtobufMessageDescriptors.registerMessageDescriptorForType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.google.protobuf.Message;

import mobi.f2time.dorado.Dorado;
import mobi.f2time.dorado.rest.DefaultParameterNameResolver;
import mobi.f2time.dorado.rest.MediaType;
import mobi.f2time.dorado.rest.ParameterNameResolver;
import mobi.f2time.dorado.rest.annotation.Consume;
import mobi.f2time.dorado.rest.annotation.Produce;
import mobi.f2time.dorado.rest.http.HttpRequest;
import mobi.f2time.dorado.rest.http.HttpResponse;
import mobi.f2time.dorado.rest.http.MultipartFile;

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
	private String consume;
	private String produce;

	private MethodDescriptor(Class<?> clazz, Method method) {
		this(clazz, method, new DefaultParameterNameResolver());
	}

	private MethodDescriptor(Class<?> clazz, Method method, ParameterNameResolver parameterNameResolver) {
		this.clazz = clazz;
		this.method = method;
		this.returnType = method.getReturnType();

		if (!method.isAccessible()) {
			method.setAccessible(true);
		}

		Class<?>[] parameterTypes = method.getParameterTypes();
		Type[] genericParameterTypes = method.getGenericParameterTypes();

		String[] parameterNames = parameterNameResolver.getParameterNames(method);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		this.invokeTarget = Dorado.beanContainer.getBean(clazz);
		this.annotations = method.getAnnotations();
		Consume consumeAnnotation = method.getAnnotation(Consume.class);
		Produce produceAnnotation = method.getAnnotation(Produce.class);

		methodParameters = new MethodParameter[method.getParameterCount()];
		for (int i = 0; i < method.getParameterCount(); i++) {
			Annotation annotation = parameterAnnotations[i].length == 0 ? null : parameterAnnotations[i][0];
			Class<?> type = parameterTypes[i];
			String name = parameterNames[i];
			Type genericParameterType = genericParameterTypes[i];

			MethodParameter methodParameter = MethodParameter.create(name, type, genericParameterType, annotation);
			if (methodParameter.annotationType == MultipartFile.class) {
			}
			methodParameters[i] = methodParameter;
			methodParameters[i].setMethodParameterCount(method.getParameterCount());
			registerMessageDescriptorForTypeIfNeed(type);
		}

		this.consume = consumeAnnotation == null ? guessConsume() : consumeAnnotation.value();
		this.produce = produceAnnotation == null ? guessProduce() : produceAnnotation.value();
	}

	private void registerMessageDescriptorForTypeIfNeed(Class<?> type) {
		try {
			if (Message.class.isAssignableFrom(type)) {
				registerMessageDescriptorForType(type);
			}
		} catch (Throwable ex) {
			// ignore this ex
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
		return this.consume;
	}

	private String guessConsume() {
		for (MethodParameter param : this.methodParameters) {
			if (param.annotationType == MultipartFile.class) {
				return MediaType.MULTIPART_FORM_DATA;
			} else if (TypeUtils.isProtobufMessage(param.getType())) {
				return MediaType.APPLICATION_PROTOBUF;
			}
		}
		return MediaType.APPLICATION_JSON;
	}

	public String produce() {
		return this.produce;
	}

	private String guessProduce() {
		MediaType mediaType = MediaTypeUtils.forType(returnType);
		return mediaType == null ? MediaType.WILDCARD : mediaType.toString();
	}

	public static class MethodParameter {
		private String name;
		private Class<?> type;
		private Annotation annotation;
		private Class<?> annotationType;
		private int methodParameterCount;
		private Type parameterizedType;

		private MethodParameter(String name, Class<?> type, Type parameterizedType, Annotation annotation) {
			this.name = name;
			this.type = type;
			this.annotation = annotation;
			this.annotationType = annotation == null ? null : annotation.annotationType();

			if (type == HttpRequest.class)
				this.annotationType = HttpRequest.class;
			if (type == HttpResponse.class)
				this.annotationType = HttpResponse.class;
			if (type == MultipartFile.class)
				this.annotationType = MultipartFile.class;
			if (type.isArray() && type.getComponentType() == MultipartFile.class) {
				this.annotationType = MultipartFile.class;
			}

			this.parameterizedType = parameterizedType;
		}

		public void setMethodParameterCount(int parameterCount) {
			this.methodParameterCount = parameterCount;
		}

		public static MethodParameter create(String name, Class<?> type, Type genericParameterType,
				Annotation annotation) {
			return new MethodParameter(name, type, genericParameterType, annotation);
		}

		public String getName() {
			return this.name;
		}

		public int getMethodParameterCount() {
			return methodParameterCount;
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

		public Type getParameterizedType() {
			return parameterizedType;
		}

		@Override
		public String toString() {
			return "MethodParameter [name=" + name + ", type=" + type + ", annotation=" + annotation
					+ ", annotationType=" + annotationType + "]";
		}
	}

	public static void main(String[] args) throws Exception {
		MultipartFile[] a = new MultipartFile[] {};
		System.out.println(a.getClass().isArray());
	}
}
