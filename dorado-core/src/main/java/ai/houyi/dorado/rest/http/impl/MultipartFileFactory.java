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
package ai.houyi.dorado.rest.http.impl;

import ai.houyi.dorado.rest.http.MultipartFile;
import io.netty.handler.codec.http.multipart.FileUpload;

/**
 * @author weiping wang
 *
 */
public final class MultipartFileFactory {

	public static MultipartFile create(FileUpload fileUpload) {
		try {
			MultipartFile _file = new MultipartFile();
			_file.setContent(fileUpload.get());
			_file.setContentType(fileUpload.getContentType());
			_file.setSize(fileUpload.length());
			_file.setName(fileUpload.getFilename());
			return _file;
		} catch (Exception ex) {
			// ignore exception
		}
		return null;
	}
}
