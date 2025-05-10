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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import ai.houyi.dorado.rest.http.MultipartFile;
import io.netty.handler.codec.http.multipart.FileUpload;

/**
 * @author weiping wang
 */
public final class MultipartFileFactory {

    public static MultipartFile create(FileUpload fileUpload) {
        if (!fileUpload.isCompleted()) {
            throw new IllegalStateException("FileUpload is not completed");
        }
        try {
            MultipartFile multipartFile = new MultipartFile();
            multipartFile.setContent(fileUpload.isInMemory() ? fileUpload.get() : null);
            multipartFile.setContentType(fileUpload.getContentType());
            multipartFile.setSize(fileUpload.length());
            multipartFile.setName(fileUpload.getFilename());
            multipartFile.setStream(toInputStream(fileUpload));
            return multipartFile;
        } catch (Exception ex) {
            // ignore exception
        }
        return null;
    }

    private static InputStream toInputStream(FileUpload fileUpload) throws IOException {
        if (fileUpload.isInMemory()) {
            return new ByteArrayInputStream(fileUpload.get());
        }
        return Files.newInputStream(fileUpload.getFile().toPath());
    }
}
