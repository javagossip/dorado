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
package mobi.f2time.dorado.rest.controller;

import java.util.ArrayList;
import java.util.List;

import mobi.f2time.dorado.rest.annotation.Controller;
import mobi.f2time.dorado.rest.annotation.Path;
import mobi.f2time.dorado.rest.router.UriRoutingPath;
import mobi.f2time.dorado.rest.router.UriRoutingRegistry;
import mobi.f2time.dorado.rest.router.UriRoutingRegistry.UriRouting;
import mobi.f2time.dorado.rest.util.StringUtils;

/**
 * 
 * @author wangwp
 */
@Controller
@Path("/")
public class RootController {
	private static final String DORADO_WELCOME = "Welcome to dorado!";

	@Path
	public String index() {
		return DORADO_WELCOME;
	}

	@Path("status")
	public DoradoStatus status() {
		return DoradoStatus.get();
	}

	@Path("services")
	public List<RestService> services() {
		List<RestService> serviceList = new ArrayList<>();

		List<UriRouting> uriRoutings = UriRoutingRegistry.getInstance().uriRoutings();
		for (UriRouting uriRouting : uriRoutings) {
			UriRoutingPath routingPath = uriRouting.uriRoutingPath();

			String path = routingPath.routingPath();
			String httpMethod = StringUtils.defaultString(routingPath.httpMethod(), "*");

			serviceList.add(RestService.builder().withPath(path).withHttpMethod(httpMethod).build());
		}
		return serviceList;
	}
}
