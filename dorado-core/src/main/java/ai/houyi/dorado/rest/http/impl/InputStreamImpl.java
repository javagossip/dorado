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

import java.io.IOException;
import java.io.InputStream;

import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 
 * @author wangwp
 */
public class InputStreamImpl extends InputStream {

	private final ByteBufInputStream in;

	public InputStreamImpl(FullHttpRequest request) {
		this.in = new ByteBufInputStream(request.content());
	}

	@Override
	public int read() throws IOException {
		return this.in.read();
	}
}
