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
package mobi.f2time.dorado.swagger.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.models.Swagger;
import mobi.f2time.dorado.rest.annotation.Controller;
import mobi.f2time.dorado.rest.annotation.Path;
import mobi.f2time.dorado.rest.annotation.Produce;
import mobi.f2time.dorado.swagger.SwaggerFactory;

/**
 * 
 * @author wangwp
 */
@Controller
@Path("/api-docs")
public class SwaggerV2Controller {
	private static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	@Path("/swagger.yaml")
	@Produce("application/yaml")
	public Swagger listingWithYaml() {
		return SwaggerFactory.getSwagger();
	}

	@Path("/swagger.json")
	@Produce("application/json")
	public String listingWithJson() {
		try {
			return mapper.writeValueAsString(SwaggerFactory.getSwagger());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
