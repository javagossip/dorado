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
package ai.houyi.dorado.rest.controller;

import java.util.ArrayList;
import java.util.List;

import ai.houyi.dorado.Dorado;
import ai.houyi.dorado.rest.annotation.Controller;
import ai.houyi.dorado.rest.annotation.GET;
import ai.houyi.dorado.rest.annotation.Path;
import ai.houyi.dorado.rest.http.impl.Webapp;
import ai.houyi.dorado.rest.router.Router.Route;
import ai.houyi.dorado.rest.server.DoradoServerBuilder;
import ai.houyi.dorado.rest.util.StringUtils;

/**
 * @author wangwp
 */
@Controller
public class RootController {

    private static final String DORADO_WELCOME = "Welcome to dorado!";

    @Path("/")
    @GET
    public String index() {
        return DORADO_WELCOME;
    }

    @Path("/status")
    @GET
    public DoradoStatus status() {
        return DoradoStatus.get();
    }

    @Path("/services")
    @GET
    public List<RestService> services() {
        List<RestService> serviceList = new ArrayList<>();

        String contextPath = Dorado.serverConfig.getContextPath();
        List<Route> routes = Webapp.get().getRouter().getRoutes();
        for (Route route : routes) {
            String path = route.getPath();
            String method = StringUtils.defaultString(route.getMethod(), "*");
            serviceList.add(RestService.builder().withPath(contextPath + path).withMethod(method).build());
        }
        return serviceList;
    }

    @GET
    @Path("/router/dump")
    public String dumpRouter() {
        return Webapp.get().getRouter().dump();
    }

    @GET
    @Path("/config")
    public DoradoServerBuilder config() {
        return Dorado.serverConfig;
    }
}
