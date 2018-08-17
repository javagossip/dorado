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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import mobi.f2time.dorado.rest.annotation.HeaderParam;
import mobi.f2time.dorado.rest.annotation.PathVariable;
import mobi.f2time.dorado.rest.annotation.RequestParam;
import mobi.f2time.dorado.rest.util.StringUtils;

/**
 * 根据方法参数注解获得方法参数名
 * 
 * @author wangwp
 */
public class DefaultParameterNameResolver implements ParameterNameResolver {
	private ParameterNameResolver parameterNameResolver;

	public DefaultParameterNameResolver() {
		this.parameterNameResolver = new LocalVariableTableParameterNameResolver();
	}

	@Override
	public String[] getParameterNames(Method method) {
		if (method.getParameterCount() == 0)
			return new String[] {};
		String[] parameterNames = parameterNameResolver.getParameterNames(method);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		Annotation parameterAnnotation = null;
		for (int i = 0; i < parameterNames.length; i++) {
			if (parameterAnnotations[i].length == 0) {
				continue;
			}
			parameterAnnotation = parameterAnnotations[i][0];
			String parameterNameByAnnotation = getParameterNameByAnnotation(parameterAnnotation);
			parameterNames[i] = StringUtils.defaultString(parameterNameByAnnotation, parameterNames[i]);
		}
		return parameterNames;
	}

	private String getParameterNameByAnnotation(Annotation annotation) {
		Class<?> annotationType = annotation.annotationType();
		if (annotationType == RequestParam.class) {
			return ((RequestParam) annotation).value();
		} else if (annotationType == HeaderParam.class) {
			return ((HeaderParam) annotation).value();
		} else if (annotationType == PathVariable.class) {
			return ((PathVariable) annotation).value();
		}
		return null;
	}
}
