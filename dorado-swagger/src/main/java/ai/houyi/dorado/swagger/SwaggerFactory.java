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
package ai.houyi.dorado.swagger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import ai.houyi.dorado.Dorado;
import ai.houyi.dorado.rest.util.PackageScanner;
import ai.houyi.dorado.swagger.ext.ApiContext;
import ai.houyi.dorado.swagger.ext.ApiContextBuilder;
import ai.houyi.dorado.swagger.ext.ApiKey;
import io.swagger.models.Scheme;
import io.swagger.models.SecurityRequirement;
import io.swagger.models.Swagger;
import io.swagger.models.auth.ApiKeyAuthDefinition;
import io.swagger.models.auth.In;

/**
 * 
 * @author wangwp
 */
public class SwaggerFactory {
	private static Swagger swagger;
	private static ApiContextBuilder apiContextBuilder;
	private static ApiContext apiContext;
	private static boolean swaggerEnable = true;

	static {
		ServiceLoader<ApiContextBuilder> apiContextBuilders = ServiceLoader.load(ApiContextBuilder.class);
		ApiContextBuilder default_ctx_builder = apiContextBuilders.iterator().hasNext()
				? apiContextBuilders.iterator().next()
				: null;

		try {
			if (Dorado.isEnableSpring) {
				apiContextBuilder = Dorado.beanContainer.getBean(ApiContextBuilder.class);
				apiContext = Dorado.beanContainer.getBean(ApiContext.class);
				if (apiContextBuilder == null)
					apiContextBuilder = default_ctx_builder;
			} else {
				apiContextBuilder = default_ctx_builder;
			}

			if (apiContext == null && apiContextBuilder != null) {
				apiContext = apiContextBuilder.buildApiContext();
			}

			if (apiContext == null)
				swaggerEnable = false;
		} catch (Throwable ex) {
			// ignore this exception
		}
	}

	public static Swagger getSwagger() {
		if (!swaggerEnable)
			return new Swagger();
		
		if (swagger != null)
			return swagger;

		Reader reader = new Reader(new Swagger());

		String[] packages = null;
		Class<?> mainClass = Dorado.mainClass;
		EnableSwagger enableSwagger = mainClass.getAnnotation(EnableSwagger.class);

		if (enableSwagger != null) {
			packages = enableSwagger.value();
		}

		if (packages == null || packages.length == 0) {
			packages = Dorado.serverConfig.scanPackages();
		}

		if (packages == null || packages.length == 0) {
			packages = new String[] { mainClass.getPackage().getName() };
		}

		if (packages == null || packages.length == 0) {
			throw new IllegalArgumentException("缺少scanPackages设置");
		}

		Set<Class<?>> classes = new HashSet<>();
		for (String pkg : packages) {
			try {
				classes.addAll(PackageScanner.scan(pkg));
			} catch (Exception ex) {
				// ignore this ex
			}
		}

		Swagger _swagger = reader.read(classes);
		_swagger.setSchemes(Arrays.asList(Scheme.HTTP, Scheme.HTTPS));

		ApiKey apiKey = apiContext.getApiKey();
		if (apiKey != null) {
			ApiKeyAuthDefinition apiKeyAuth = new ApiKeyAuthDefinition(apiKey.getName(),
					In.forValue(apiKey.getIn() == null ? "header" : apiKey.getIn()));
			_swagger.securityDefinition("auth", apiKeyAuth);

			List<SecurityRequirement> securityRequirements = new ArrayList<>();
			SecurityRequirement sr = new SecurityRequirement();
			sr.requirement("auth");
			securityRequirements.add(sr);
			_swagger.setSecurity(securityRequirements);
		}
		if (apiContext.getInfo() != null)
			_swagger.setInfo(apiContext.getInfo());

		swagger = _swagger;
		return _swagger;
	}
}
