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
package mobi.f2time.dorado.example.controller;

import mobi.f2time.dorado.rest.annotation.Controller;
import mobi.f2time.dorado.rest.annotation.POST;
import mobi.f2time.dorado.rest.annotation.Path;
import mobi.f2time.dorado.rest.annotation.Produce;
import mobi.f2time.dorado.rest.http.MultipartFile;
import mobi.f2time.dorado.rest.util.FileUtils;

/**
 * @author weiping wang
 *
 */
@Controller
@Path("/file/upload")
public class FileUploadController {
	
	//@Produce("image/jpeg")
	@POST
	public byte[] upload(MultipartFile file) {
		System.out.println(file);
		System.out.println(file.getContent().length);
		
		//saveFile(file);
		return file.getContent();
	}
}
