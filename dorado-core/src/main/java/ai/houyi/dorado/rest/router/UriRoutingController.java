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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ai.houyi.dorado.exception.DoradoException;
import ai.houyi.dorado.rest.MediaType;
import ai.houyi.dorado.rest.MessageBodyConverter;
import ai.houyi.dorado.rest.MessageBodyConverters;
import ai.houyi.dorado.rest.ParameterValueResolver;
import ai.houyi.dorado.rest.ParameterValueResolvers;
import ai.houyi.dorado.rest.annotation.Status;
import ai.houyi.dorado.rest.http.HttpRequest;
import ai.houyi.dorado.rest.http.HttpResponse;
import ai.houyi.dorado.rest.http.MethodReturnValueHandler;
import ai.houyi.dorado.rest.http.MethodReturnValueHandlerConfig;
import ai.houyi.dorado.rest.http.impl.ExceptionHandler;
import ai.houyi.dorado.rest.http.impl.HttpHeaderNames;
import ai.houyi.dorado.rest.http.impl.WebComponentRegistry;
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

	public Object invoke(HttpRequest request, HttpResponse response, String[] pathVariables) throws Exception {
		Method invokeMethod = methodDescriptor.getMethod();
		MediaType expectedMediaType = MediaType.valueOf(methodDescriptor.consume());

		// 如果非GET、DELETE请求需要验证请求的内容类型是否匹配
		if (!HttpMethod.GET.name().equals(request.getMethod()) && !HttpMethod.DELETE.name().equals(request.getMethod())
				&& (!expectedMediaType.isWildcardType())) {
			String contentType = request.getHeader(HttpHeaderNames.CONTENT_TYPE);
			MediaType requestMediaType = MediaType.valueOf(contentType);

			if (requestMediaType != null && !requestMediaType.isCompatible(expectedMediaType)) {
				throw new DoradoException(String.format("Invalid request content_type, expected: [%s], actual: [%s]",
						methodDescriptor.consume(), contentType));
			}
		}

		Object[] args = resolveParameters(request, response, pathVariables);
		MethodReturnValueHandlerConfig methodReturnValueHandlerConfig = Webapp.get()
				.getMethodReturnValueHandlerConfig();

		try {
			Object invokeResult = invokeMethod.invoke(methodDescriptor.getInvokeTarget(), args);
			// 支持对controller返回结果进行统一处理
			MediaType mediaType = MediaTypeUtils.defaultForType(methodDescriptor.getReturnType(),
					methodDescriptor.produce());

			if (supportHandleMethodReturnValue(request, methodDescriptor, methodReturnValueHandlerConfig)) {
				invokeResult = methodReturnValueHandlerConfig.getHandler().handleMethodReturnValue(invokeResult,
						methodDescriptor);
				if (invokeResult != null) {
					mediaType = MediaTypeUtils.defaultForType(invokeResult.getClass(), null);
				}
			}
			if (methodReturnValueHandlerConfig != null
					&& !methodReturnValueHandlerConfig.exclude(request.getRequestURI())) {
			}
			writeResponseBody(invokeResult, mediaType, response);
		} catch (Exception ex) {
			handleException(ex, request, response);
		}
		return null;
	}

	private boolean supportHandleMethodReturnValue(HttpRequest request, MethodDescriptor methodDescriptor,
			MethodReturnValueHandlerConfig methodReturnValueHandlerConfig) {
		if (methodReturnValueHandlerConfig == null)
			return false;

		MethodReturnValueHandler handler = methodReturnValueHandlerConfig.getHandler();
        return !methodReturnValueHandlerConfig.exclude(request.getRequestURI()) &&
                handler.supportsReturnType(methodDescriptor);
    }

	private void handleException(Exception ex, HttpRequest request, HttpResponse response) {
		Throwable targetException = ex;
		if (ex instanceof InvocationTargetException) {
			targetException = ((InvocationTargetException) ex).getTargetException();
		}

		ExceptionHandler exceptionHandler = WebComponentRegistry.getWebComponentRegistry()
				.getExceptionHandler(targetException.getClass());
		if (exceptionHandler == null) {
			throw new DoradoException(targetException);
		}

		Object exceptionHandleResult = exceptionHandler.handleException(targetException);
		if (exceptionHandleResult == null) {
			throw new DoradoException(targetException);
		}

		MediaType mediaType = MediaTypeUtils.defaultForType(exceptionHandleResult.getClass(),
				exceptionHandler.produce());
		Status status = exceptionHandler.status();
		if (status != null)
			response.setStatus(status.value());

		writeResponseBody(exceptionHandleResult, mediaType, response);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void writeResponseBody(Object body, MediaType mediaType, HttpResponse response) {
		if (body == null)
			return;
		MessageBodyConverter messageBodyConverter = MessageBodyConverters.getMessageBodyConverter(mediaType);
		response.setHeader(HttpHeaderNames.CONTENT_TYPE, mediaType.toString());
		response.write(messageBodyConverter.writeMessageBody(body));
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
