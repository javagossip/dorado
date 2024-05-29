/*
 * Copyright 2012 The OpenDSP Project
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
package ai.houyi.dorado.swagger.ui.controller;

import ai.houyi.dorado.rest.annotation.Controller;
import ai.houyi.dorado.rest.annotation.GET;
import ai.houyi.dorado.rest.annotation.Path;
import ai.houyi.dorado.rest.http.HttpRequest;
import ai.houyi.dorado.rest.http.HttpResponse;
import ai.houyi.dorado.rest.util.ClassLoaderUtils;
import ai.houyi.dorado.rest.util.IOUtils;

/**
 * @author weiping wang
 */
@Controller
@Path("/swagger-ui.*")
public class SwaggerUIController {

    private static final String RESOURCE_PREFIX = "META-INF/resources/webjars/swagger-ui";

    @GET
    @Path
    public void readStaticResource(HttpRequest request, HttpResponse response) {
        String uri = request.getRequestURI();

        String resource = String.format("%s%s", RESOURCE_PREFIX, uri);
        byte[] data = IOUtils.readBytes(ClassLoaderUtils.getStream(resource));
        if (uri.endsWith(".css")) {
            response.setHeader("content-type", "text/css;charset=UTF-8");
        } else if (uri.endsWith(".html")) {
            response.setHeader("content-type", "text/html;charset=UTF-8");
        } else if (uri.endsWith(".js")) {
            response.setHeader("content-type", "text/javascript;charset=UTF-8");
        }  //DO NOTHING

        response.write(data);
    }
}