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
package ai.houyi.dorado.rest.http;

import ai.houyi.dorado.rest.util.IOUtils;

import java.io.InputStream;

/**
 * @author weiping wang
 */
public class MultipartFile {

    private String name;
    private String contentType;
    private byte[] content;
    private InputStream stream;
    private long size;

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MultipartFile [name=" + name + ", contentType=" + contentType + ", size=" + size + "]";
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        if (content != null) {
            return content;
        }
        if (getStream() != null) {
            content = IOUtils.readBytes(getStream());
        }
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }
}
