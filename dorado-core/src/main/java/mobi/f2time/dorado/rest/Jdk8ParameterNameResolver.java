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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 
 * @author wangwp
 */
public class Jdk8ParameterNameResolver implements ParameterNameResolver {

	@Override
	public String[] getParameterNames(Method method) {
		Parameter[] methodParameters = method.getParameters();
		String[] parameterNames = new String[methodParameters.length];

		for (int i = 0; i < parameterNames.length; i++) {
			parameterNames[i] = methodParameters[i].getName();
		}
		
		return parameterNames;
	}

}
