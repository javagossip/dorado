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
package ai.houyi.dorado.rest.router;

import java.lang.reflect.Method;

import ai.houyi.dorado.exception.DoradoException;
import ai.houyi.dorado.rest.MediaType;
import ai.houyi.dorado.rest.MessageBodyConverter;
import ai.houyi.dorado.rest.MessageBodyConverters;
import ai.houyi.dorado.rest.ParameterValueResolver;
import ai.houyi.dorado.rest.ParameterValueResolvers;
import ai.houyi.dorado.rest.http.ExceptionHandler;
import ai.houyi.dorado.rest.http.HttpRequest;
import ai.houyi.dorado.rest.http.HttpResponse;
import ai.houyi.dorado.rest.http.MethodReturnValueHandler;
import ai.houyi.dorado.rest.http.impl.HttpHeaderNames;
import ai.houyi.dorado.rest.http.impl.Webapp;
import ai.houyi.dorado.rest.util.MediaTypeUtils;
import ai.houyi.dorado.rest.util.MethodDescriptor;
import ai.houyi.dorado.rest.util.MethodDescriptor.MethodParameter;
import io.netty.handler.codec.http.HttpMethod;

/**
 * 
 * @author wangwp
 */
public class UriRoutingController {
	private final MethodDescriptor methodDescriptor;
	private final UriRoutingPath uriRoutingPath;

	private UriRoutingController(UriRoutingPath uriRoutingPath, Class<?> clazz, Method method) {
		methodDescriptor = MethodDescriptor.create(clazz, method);
		this.uriRoutingPath = uriRoutingPath;
	}

	public static UriRoutingController create(UriRoutingPath uriRoutingPath, Class<?> clazz, Method method) {
		return new UriRoutingController(uriRoutingPath, clazz, method);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object invoke(HttpRequest request, HttpResponse response, String[] pathVariables) throws Exception {
		Method invokeMethod = methodDescriptor.getMethod();
		MediaType expectedMediaType = MediaType.valueOf(methodDescriptor.consume());

		// 如果非GET、DELETE请求需要验证请求的内容类型是否匹配
		if (!HttpMethod.GET.name().equals(request.getMethod()) && !HttpMethod.DELETE.name().equals(request.getMethod())
				&& (!expectedMediaType.isWildcardType())) {
			String contentType = request.getHeader(HttpHeaderNames.CONTENT_TYPE);
			MediaType requestMediaType = MediaType.valueOf(contentType);

			if (!requestMediaType.isCompatible(expectedMediaType) || requestMediaType == null) {
				throw new DoradoException(String.format("Invalid request content_type, expected: [%s], actual: [%s]",
						methodDescriptor.consume(), contentType));
			}
		}

		Object[] args = resolveParameters(request, response, pathVariables);
		
		try {
			if (methodDescriptor.getReturnType() == void.class) {
				invokeMethod.invoke(methodDescriptor.getInvokeTarget(), args);
			} else {
				Object result = invokeMethod.invoke(methodDescriptor.getInvokeTarget(), args);
				// 支持对controller返回结果进行统一处理
				MethodReturnValueHandler methodReturnValueHandler = Webapp.get().getMethodReturnValueHandler();
				if (methodReturnValueHandler != null) {
					result = methodReturnValueHandler.handleMethodReturnValue(result, methodDescriptor);
				}
				MediaType mediaType = MediaTypeUtils.defaultForType(methodDescriptor.getReturnType(),
						methodDescriptor.produce());

				MessageBodyConverter messageBodyConverter = MessageBodyConverters.getMessageBodyConverter(mediaType);
				response.setHeader(HttpHeaderNames.CONTENT_TYPE, mediaType.toString());
				response.write(messageBodyConverter.writeMessageBody(result));
			}
		} catch (Exception ex) {
			ExceptionHandler exceptionHandler = Webapp.get().getExceptionHandler();
			if (exceptionHandler == null) {
				throw ex;
			}
			exceptionHandler.handleException(ex, response);
		}
		return null;
	}

	private Object[] resolveParameters(HttpRequest request, HttpResponse response, String[] pathVariables) {
		MethodParameter[] methodParameters = methodDescriptor.getParameters();
		if (methodParameters.length == 0)
			return null;
		Object[] methodArgs = new Object[methodParameters.length];
		for (int i = 0; i < methodArgs.length; i++) {
			int pathVariableIndex = uriRoutingPath.resolvePathIndex(methodParameters[i].getName());
			methodArgs[i] = resolveMethodArg(request, response, methodDescriptor, methodParameters[i],
					pathVariableIndex == -1 ? null : pathVariables[pathVariableIndex]);
		}
		return methodArgs;
	}

	private Object resolveMethodArg(HttpRequest request, HttpResponse response, MethodDescriptor desc,
			MethodParameter methodParameter, String pathVariable) {
		Class<?> parameterAnnotationType = methodParameter.getAnnotationType();
		ParameterValueResolver parameterValueResolver = ParameterValueResolvers
				.getParameterValueResolver(parameterAnnotationType);

		return parameterValueResolver.resolveParameterValue(request, response, desc, methodParameter, pathVariable);
	}

	@Override
	public String toString() {
		return "RouteController [methodDescriptor=" + methodDescriptor + "]";
	}
}
