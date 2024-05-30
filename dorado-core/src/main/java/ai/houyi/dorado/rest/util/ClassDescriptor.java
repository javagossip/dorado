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
package ai.houyi.dorado.rest.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author wangwp
 */
public class ClassDescriptor {
	private Class<?> type;
	private String name;

	private Annotation[] annotations;
	private List<MethodDescriptor> methodDescriptors;

	public ClassDescriptor(Class<?> type) {
		this.type = type;
		this.name = type.getName();

		this.annotations = type.getAnnotations();
		methodDescriptors = getMethods();
	}

	private List<MethodDescriptor> getMethods() {
		List<MethodDescriptor> methodDescriptorList = new ArrayList<>();

		Method[] methods = type.getDeclaredMethods();
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers()) || method.getAnnotations().length == 0
					|| !Modifier.isPublic(method.getModifiers())) {
				continue;
			}
			methodDescriptorList.add(MethodDescriptor.create(type, method));
		}
		return methodDescriptorList;
	}

	public static ClassDescriptor create(Class<?> type) {
		return new ClassDescriptor(type);
	}

	public Class<?> getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public List<MethodDescriptor> getMethodDescriptors() {
		return methodDescriptors;
	}
}
