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

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import ai.houyi.dorado.netty.ext.HttpPostRequestDecoder;
import ai.houyi.dorado.rest.http.HttpRequest;
import ai.houyi.dorado.rest.http.MultipartFile;
import ai.houyi.dorado.rest.util.LogUtils;
import ai.houyi.dorado.rest.util.NetUtils;
import ai.houyi.dorado.rest.util.StringUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;

/**
 * 
 * @author wangwp
 */
public class HttpRequestImpl implements HttpRequest {
	private final FullHttpRequest request;

	private final InputStreamImpl in;

	private final QueryStringDecoder queryStringDecoder;

	private final URIParser uriParser;

	private final Map<String, List<String>> parameters;

	private final HttpHeaders headers;
	private final List<MultipartFile> multipartFiles;

	public HttpRequestImpl(FullHttpRequest request) {
		this.request = request;
		this.parameters = new HashMap<>();
		this.headers = request.headers();
		this.multipartFiles = new ArrayList<>();

		this.uriParser = new URIParser();

		// 解析querystring上面的参数
		queryStringDecoder = new QueryStringDecoder(request.uri());
		uriParser.parse(queryStringDecoder.path());
		parameters.putAll(queryStringDecoder.parameters());
		in = new InputStreamImpl(request);

		if (request.method() == HttpMethod.POST) {
			parseHttpPostRequest(request);
		}
	}

	private void parseHttpPostRequest(FullHttpRequest request) {
		HttpPostRequestDecoder decoder = null;
		try {
			decoder = new HttpPostRequestDecoder(request);
			for (InterfaceHttpData httpData : decoder.getBodyHttpDatas()) {
				HttpDataType _type = httpData.getHttpDataType();
				if (_type == HttpDataType.Attribute) {
					Attribute attribute = (Attribute) httpData;
					parseAttribute(attribute);
				} else if (_type == HttpDataType.FileUpload) {
					FileUpload upload = (FileUpload) httpData;
					multipartFiles.add(MultipartFileFactory.create(upload));
				}
			}
		} catch (Exception ex) {
			LogUtils.warn(ex.getMessage());
		} finally {
			// 注意这个地方，一定要调用destroy方法，如果不调用会导致内存泄漏
			if (decoder != null)
				decoder.destroy();
		}
	}

	private void parseAttribute(Attribute attribute) throws Exception {
		if (this.parameters.containsKey(attribute.getName())) {
			this.parameters.get(attribute.getName()).add(attribute.getValue());
		} else {
			List<String> values = new ArrayList<>();
			values.add(attribute.getValue());
			this.parameters.put(attribute.getName(), values);
		}
	}

	@Override
	public String getParameter(String key) {
		List<String> parameterValues = parameters.get(key);
		return (parameterValues == null || parameterValues.isEmpty()) ? null : parameterValues.get(0);
	}

	@Override
	public String[] getParameterValues(String key) {
		return this.parameters.get(key).toArray(new String[] {});
	}

	@Override
	public Map<String, List<String>> getParameters() {
		return this.parameters;
	}

	@Override
	public String getRemoteAddr() {
		InetSocketAddress addr = (InetSocketAddress) ChannelHolder.get().remoteAddress();
		String fallbackAddr = addr.getAddress().getHostAddress();

		String xForwardFor = headers.get("X-Forwarded-For");
		if (xForwardFor == null || StringUtils.isBlank(xForwardFor)) {
			return fallbackAddr;
		}

		String[] proxyIpList = xForwardFor.split(",");
		for (int i = proxyIpList.length - 1; i >= 0; i--) {
			String proxyIp = proxyIpList[i];
			if (!StringUtils.isBlank(proxyIp))
				proxyIp = proxyIp.trim();
			if (!NetUtils.isInternalIp(proxyIp)) {
				return proxyIp;
			}
		}
		return fallbackAddr;
	}

	@Override
	public ai.houyi.dorado.rest.http.Cookie[] getCookies() {
		String cookieString = this.request.headers().get(HttpHeaderNames.COOKIE);
		if (cookieString != null) {
			Set<Cookie> cookies = ServerCookieDecoder.LAX.decode(cookieString);
			return cookies.stream().map(cookie -> new CookieImpl(cookie)).collect(Collectors.toList())
					.toArray(new CookieImpl[] {});
		}
		return null;
	}

	@Override
	public String getHeader(String name) {
		return headers.get(name);
	}

	@Override
	public String[] getHeaders(String name) {
		return headers.getAll(name).toArray(new String[] {});
	}

	@Override
	public String getQueryString() {
		return uriParser.getQueryString();
	}

	@Override
	public String getRequestURI() {
		return uriParser.getRequestUri();
	}

	@Override
	public String getMethod() {
		return this.request.method().name();
	}

	@Override
	public InputStream getInputStream() {
		return this.in;
	}

	public MultipartFile getFile() {
		if (multipartFiles != null && !multipartFiles.isEmpty()) {
			return multipartFiles.get(0);
		}
		return null;
	}

	public MultipartFile[] getFiles() {
		if (multipartFiles != null && !multipartFiles.isEmpty()) {
			return multipartFiles.toArray(new MultipartFile[] {});
		}
		return null;
	}

	public List<MultipartFile> getFileList() {
		return multipartFiles;
	}
}
