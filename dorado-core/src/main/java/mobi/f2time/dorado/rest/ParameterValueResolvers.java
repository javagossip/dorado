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
package mobi.f2time.dorado.rest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import mobi.f2time.dorado.rest.annotation.HeaderParam;
import mobi.f2time.dorado.rest.annotation.PathVariable;
import mobi.f2time.dorado.rest.annotation.RequestBody;
import mobi.f2time.dorado.rest.annotation.RequestParam;
import mobi.f2time.dorado.rest.http.HttpRequest;
import mobi.f2time.dorado.rest.http.HttpResponse;
import mobi.f2time.dorado.rest.http.MultipartFile;

/**
 * 
 * @author wangwp
 */
public class ParameterValueResolvers {
	private static Map<Class<?>, ParameterValueResolver> parameterValueResolverHolder = new ConcurrentHashMap<>();

	static {
		parameterValueResolverHolder.put(RequestParam.class, ParameterValueResolver.REQUEST_PARAM);
		parameterValueResolverHolder.put(HeaderParam.class, ParameterValueResolver.HEADER_PARAM);
		parameterValueResolverHolder.put(PathVariable.class, ParameterValueResolver.PATH_PARAM);
		parameterValueResolverHolder.put(HttpRequest.class, ParameterValueResolver.HTTP_REQUEST);
		parameterValueResolverHolder.put(HttpResponse.class, ParameterValueResolver.HTTP_RESPONSE);
		parameterValueResolverHolder.put(RequestBody.class, ParameterValueResolver.REQUEST_BODY);
		parameterValueResolverHolder.put(MultipartFile.class, ParameterValueResolver.MULTIPARTFILE);
	}

	public static ParameterValueResolver getParameterValueResolver(Class<?> parameterAnnotationType) {
		if (parameterAnnotationType == null) {
			return ParameterValueResolver.ALL;
		}

		ParameterValueResolver parameterValueResolver = parameterValueResolverHolder.get(parameterAnnotationType);
		if (parameterValueResolver == null) {
			return ParameterValueResolver.ALL;
		}

		return parameterValueResolverHolder.get(parameterAnnotationType);
	}

}
