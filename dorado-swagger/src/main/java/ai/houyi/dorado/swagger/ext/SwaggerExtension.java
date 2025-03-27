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
package ai.houyi.dorado.swagger.ext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ai.houyi.dorado.rest.util.MethodDescriptor.MethodParameter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import ai.houyi.dorado.rest.util.MethodDescriptor;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.Operation;
import io.swagger.models.parameters.Parameter;

/**
 * 
 * @author wangwp
 */
public interface SwaggerExtension {

	default public String extractOperationMethod(ApiOperation apiOperation, Method method,
			Iterator<SwaggerExtension> chain) {
		if (chain.hasNext()) {
			return chain.next().extractOperationMethod(apiOperation, method, chain);
		} else {
			return null;
		}
	}

	default public List<Parameter> extractParameters(List<Annotation> annotations, Type type, Set<Type> typesToSkip,
			Iterator<SwaggerExtension> chain) {
		if (chain.hasNext()) {
			return chain.next().extractParameters(annotations, type, typesToSkip, chain);
		} else {
			return Collections.emptyList();
		}
	}

	default public void decorateOperation(Operation operation, Method method, Iterator<SwaggerExtension> chain) {
		if (chain.hasNext()) {
			chain.next().decorateOperation(operation, method, chain);
		}
	}

	default boolean shouldIgnoreClass(Class<?> cls) {
		return false;
	}

	default boolean shouldIgnoreType(Type type, Set<Type> typesToSkip) {
		if (typesToSkip.contains(type)) {
			return true;
		}
		if (shouldIgnoreClass(constructType(type).getRawClass())) {
			typesToSkip.add(type);
			return true;
		}
		return false;
	}

	default JavaType constructType(Type type) {
		return TypeFactory.defaultInstance().constructType(type);
	}

	public List<Parameter> extractParameters(List<Annotation> annotations, Type type, Set<Type> typesToSkip,
			Iterator<SwaggerExtension> chain, MethodParameter methodParameter,String operationPath);
}
