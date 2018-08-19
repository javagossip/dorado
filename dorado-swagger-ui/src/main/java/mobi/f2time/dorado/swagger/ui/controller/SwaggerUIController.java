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
package mobi.f2time.dorado.swagger.ui.controller;

import mobi.f2time.dorado.rest.annotation.Controller;
import mobi.f2time.dorado.rest.annotation.GET;
import mobi.f2time.dorado.rest.annotation.Path;
import mobi.f2time.dorado.rest.annotation.Produce;
import mobi.f2time.dorado.rest.http.HttpRequest;
import mobi.f2time.dorado.rest.http.HttpResponse;
import mobi.f2time.dorado.rest.util.ClassLoaderUtils;
import mobi.f2time.dorado.rest.util.IOUtils;
import mobi.f2time.dorado.rest.util.LogUtils;

/**
 * @author weiping wang <javagossip@gmail.com>
 *
 */
@Controller
@Path("/swagger-ui.*")
public class SwaggerUIController {
	private static final String RESOURCE_PREFIX = "META-INF/resources/webjars/swagger-ui";

	
	@GET
	public void readStaticResource(HttpRequest request,HttpResponse response) {
		String uri = request.getRequestURI();
		LogUtils.info(String.format("access static resource uri: %s", uri));
		
		String resource=String.format("%s%s", RESOURCE_PREFIX,uri);
		byte[] data = IOUtils.readBytes(ClassLoaderUtils.getStream(resource));
		
		if(uri.endsWith(".css")) {
			response.setHeader("content-type", "text/css;charset=UTF-8");
		}else if(uri.endsWith(".html")) {
			response.setHeader("content-type", "text/html;charset=UTF-8");
		}else if(uri.endsWith(".js")) {
			response.setHeader("content-type", "text/javascript;charset=UTF-8");
		}else if(uri.endsWith(".map")) {
			//
		}
		response.write(data);
	}
}
	
	/*@Path(".*\\.css.*")
	@GET
	@Produce("text/css")
	public String readCss(HttpRequest request) {
		String cssUri = request.getRequestURI();
		LogUtils.info("cssUri: " + cssUri);

		String cssBody = ClassLoaderUtils.getResoureAsString(String.format("%s%s", RESOURCE_PREFIX, cssUri));
		return cssBody;
	}

	@Path(".*\\.htm.*")
	@GET
	@Produce("text/html; charset=UTF-8")
	public String indexSwaggerUi(HttpRequest request) {
		String uri = request.getRequestURI();
		LogUtils.info("uri: " + uri);
		String body = ClassLoaderUtils.getResoureAsString(String.format("%s%s", RESOURCE_PREFIX, uri));
		return body;
	}

	@Path(".*\\.js.*")
	@GET
	@Produce("text/javascript; charset=UTF-8")
	public String readJs(HttpRequest request) {
		String uri = request.getRequestURI();
		LogUtils.info("uri: " + uri);
		String body = ClassLoaderUtils.getResoureAsString(String.format("%s%s", RESOURCE_PREFIX, uri));
		return body;
	}


//	@GET
//	public void readBytes(HttpRequest request,HttpResponse response) {
//		String uri = request.getRequestURI();
//		String contentType = request.getHeader("accept");
//		LogUtils.info("uri: " + uri+", contentType: "+contentType);
//		
//		response.setHeader("content_type", contentType.split(",")[0]);
//		byte[] body=IOUtils.readBytes(ClassLoaderUtils.getStream(String.format("%s%s", RESOURCE_PREFIX, uri)));
//		response.write(body);
//		//return body;
//	}*/
//}
