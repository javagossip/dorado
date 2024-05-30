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
import java.util.Set;

import ai.houyi.dorado.Dorado;
import ai.houyi.dorado.rest.annotation.Controller;
import ai.houyi.dorado.rest.annotation.Path;
import ai.houyi.dorado.rest.router.Route;
import ai.houyi.dorado.rest.router.Router;
import ai.houyi.dorado.rest.server.DoradoServerBuilder;
import ai.houyi.dorado.rest.util.StringUtils;

/**
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

        List<Route> allRoutes = Router.getInstance().getRoutes();
        for (Route route : allRoutes) {
            String path = route.getPath();
            String method = StringUtils.defaultString(route.getMethod(), "*");
            serviceList.add(RestService.builder().withPath(path).withMethod(method).build());
        }
        return serviceList;
    }

    @Path("config")
    public DoradoServerBuilder config() {
        return Dorado.serverConfig;
    }
}
