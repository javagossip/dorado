/*
 * Copyright 2017-2019 The OpenAds Project
 *
 * The OpenAds Project licenses this file to you under the Apache License,
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
package ai.houyi.dorado.swagger.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ai.houyi.dorado.swagger.ext.ApiContext;
import ai.houyi.dorado.swagger.ext.ApiKey;
import ai.houyi.dorado.swagger.springboot.SwaggerProperties.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;

/**
 *
 * @author weiping wang
 */
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

	@Autowired
	private SwaggerProperties swaggerConfig;

	@Bean
	public ApiContext buildApiContext() {
		Info info = new Info();
		info.title(swaggerConfig.getTitle()).description(swaggerConfig.getDescription())
				.termsOfService(swaggerConfig.getTermsOfServiceUrl()).version(swaggerConfig.getVersion());
		Contact contact = swaggerConfig.getContact();

		if (contact != null)
			info.setContact(new io.swagger.models.Contact().email(contact.getEmail()).name(contact.getName())
					.url(contact.getUrl()));

		info.setLicense(new License().name(swaggerConfig.getLicense()).url(swaggerConfig.getLicenseUrl()));

		ApiKey apiKey = ApiKey.builder().withIn(swaggerConfig.getApiKey().getIn())
				.withName(swaggerConfig.getApiKey().getName()).build();

		ApiContext apiContext = ApiContext.builder().withInfo(info).withApiKey(apiKey).build();
		return apiContext;
	}
}
