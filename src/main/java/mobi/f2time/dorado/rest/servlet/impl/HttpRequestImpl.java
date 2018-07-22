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
package mobi.f2time.dorado.rest.servlet.impl;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.CharsetUtil;
import mobi.f2time.dorado.rest.servlet.HttpRequest;

/**
 * 
 * @author wangwp
 */
public class HttpRequestImpl implements HttpRequest {
	private final FullHttpRequest originalRequest;

	private final InputStreamImpl in;

	private final QueryStringDecoder queryStringDecoder;

	private final URIParser uriParser;

	private final Map<String, List<String>> parameters;

	private final HttpHeaders headers;

	public HttpRequestImpl(FullHttpRequest originalHttpRequest) {
		this.originalRequest = originalHttpRequest;
		this.parameters = new HashMap<>();

		this.in = new InputStreamImpl(originalHttpRequest);
		// 解析querystring上面的参数
		this.queryStringDecoder = new QueryStringDecoder(originalHttpRequest.uri());
		this.parameters.putAll(queryStringDecoder.parameters());

		if (originalHttpRequest.method() == HttpMethod.POST) {
			QueryStringDecoder requestParameterDecoder = new QueryStringDecoder(
					originalHttpRequest.content().toString(CharsetUtil.UTF_8), false);
			this.parameters.putAll(requestParameterDecoder.parameters());
		}

		this.headers = originalHttpRequest.headers();
		this.uriParser = new URIParser();
		uriParser.parse(originalHttpRequest.uri());
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
		String xForwardFor = headers.get("X-Forwarded-For");
		if (xForwardFor == null) {
			InetSocketAddress addr = (InetSocketAddress) ChannelHolder.get().remoteAddress();
			return addr.getAddress().getHostAddress();
		}

		return xForwardFor.split(",")[0];
	}

	@Override
	public mobi.f2time.dorado.rest.servlet.Cookie[] getCookies() {
		String cookieString = this.originalRequest.headers().get(HttpHeaderNames.COOKIE);
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
		return this.originalRequest.method().name();
	}

	@Override
	public InputStream getInputStream() {
		return this.in;
	}
}
