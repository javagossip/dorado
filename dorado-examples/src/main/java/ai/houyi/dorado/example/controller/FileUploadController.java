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
package ai.houyi.dorado.example.controller;

import ai.houyi.dorado.rest.annotation.Controller;
import ai.houyi.dorado.rest.annotation.POST;
import ai.houyi.dorado.rest.annotation.Path;
import ai.houyi.dorado.rest.annotation.Produce;
import ai.houyi.dorado.rest.http.MultipartFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author weiping wang
 *
 */
@Controller
@Path("/file/upload")
@Api(tags = { "文件上传" })
public class FileUploadController {

	@Produce("image/jpeg")
	@POST
	@Path("/single")
	@ApiOperation("文件上传测试")
	public byte[] upload(MultipartFile file) {
		System.out.println(file);
		System.out.println(file.getContent().length);

		return file.getContent();
	}

	@POST
	@Path("/multi")
	@ApiOperation("多文件上传")
	@Produce("application/json")
	public String multiUpload(MultipartFile[] files) {
		for (MultipartFile mf : files) {
			System.out.println(mf.getName() + "," + mf.getContentType());
		}
		return "OK";
	}
}
