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
package mobi.f2time.dorado.rest.router;

import java.lang.reflect.Method;

import io.netty.handler.codec.http.HttpHeaderNames;
import mobi.f2time.dorado.rest.MediaType;
import mobi.f2time.dorado.rest.MessageBodyConverter;
import mobi.f2time.dorado.rest.MessageBodyConverters;
import mobi.f2time.dorado.rest.ParameterValueResolver;
import mobi.f2time.dorado.rest.ParameterValueResolvers;
import mobi.f2time.dorado.rest.servlet.HttpRequest;
import mobi.f2time.dorado.rest.servlet.HttpResponse;
import mobi.f2time.dorado.rest.util.MediaTypeUtils;
import mobi.f2time.dorado.rest.util.MethodDescriptor;
import mobi.f2time.dorado.rest.util.MethodDescriptor.MethodParameter;

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
		Object[] args = resolveParameters(request, response, pathVariables);

		if (methodDescriptor.getReturnType() == void.class) {
			invokeMethod.invoke(methodDescriptor.getInvokeTarget(), args);
		} else {
			Object result = invokeMethod.invoke(methodDescriptor.getInvokeTarget(), args);
			MediaType mediaType = MediaTypeUtils.defaultForType(methodDescriptor.getReturnType(),
					methodDescriptor.produce());

			MessageBodyConverter messageBodyConverter = MessageBodyConverters.getMessageBodyConverter(mediaType);
			response.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), mediaType.toString());
			response.write(messageBodyConverter.writeMessageBody(result));
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
