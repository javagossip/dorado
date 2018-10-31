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
import java.io.PrintWriter;

import ai.houyi.dorado.exception.DoradoException;
import ai.houyi.dorado.rest.http.HttpResponse;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 
 * @author wangwp
 */
public class HttpResponseImpl implements HttpResponse {
	private FullHttpResponse originalHttpResponse;

	private OutputStreamImpl out;
	private PrintWriterImpl writer;

	public HttpResponseImpl(FullHttpResponse response) {
		this.originalHttpResponse = response;
		//out = new OutputStreamImpl(response);
		//writer = new PrintWriterImpl(out);
	}

	@Override
	public void setHeader(String name, String value) {
		originalHttpResponse.headers().set(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		originalHttpResponse.headers().add(name, value);
	}

	@Override
	public void sendRedirect(String location) {
		originalHttpResponse.setStatus(HttpResponseStatus.FOUND);
		originalHttpResponse.headers().set(HttpHeaderNames.LOCATION, location);
	}

	@Override
	public void sendError(int sc, String msg) {
		originalHttpResponse.setStatus(HttpResponseStatus.valueOf(sc, msg));
	}

	@Override
	public void sendError(int sc) {
		originalHttpResponse.setStatus(HttpResponseStatus.valueOf(sc));
	}

	@Override
	public void setStatus(int sc) {
		originalHttpResponse.setStatus(HttpResponseStatus.valueOf(sc));
	}

	@Override
	public OutputStreamImpl getOutputStream() throws IOException {
		return out;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return writer;
	}

	@Override
	public void write(byte[] content) {
		originalHttpResponse.content().writeBytes(content);
	}

	@Override
	public void writeStringUtf8(String str) {
		ByteBufUtil.writeUtf8(originalHttpResponse.content(), str);
	}

	@Override
	public void write(InputStream in) {
		try {
			originalHttpResponse.content().writeBytes(in, in.available());
		} catch (IOException ex) {
			throw new DoradoException(ex);
		}
	}

}
