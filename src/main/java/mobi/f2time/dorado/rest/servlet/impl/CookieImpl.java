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

import mobi.f2time.dorado.rest.servlet.Cookie;

/**
 * 
 * @author wangwp
 */
public class CookieImpl implements Cookie {
	private final io.netty.handler.codec.http.cookie.Cookie cookie;

	public CookieImpl(io.netty.handler.codec.http.cookie.Cookie cookie) {
		this.cookie = cookie;
	}

	@Override
	public String name() {
		return cookie.name();
	}

	@Override
	public String value() {
		return cookie.value();
	}

	@Override
	public void setValue(String value) {
		cookie.setValue(value);
	}

	@Override
	public boolean wrap() {
		return cookie.wrap();
	}

	@Override
	public void setWrap(boolean wrap) {
		cookie.setWrap(wrap);
	}

	@Override
	public String domain() {
		return cookie.domain();
	}

	@Override
	public void setDomain(String domain) {
		cookie.setDomain(domain);
	}

	@Override
	public String path() {
		return cookie.path();
	}

	@Override
	public void setPath(String path) {
		cookie.setPath(path);
	}

	@Override
	public long maxAge() {
		return cookie.maxAge();
	}

	@Override
	public void setMaxAge(long maxAge) {
		cookie.setMaxAge(maxAge);
	}

	@Override
	public boolean isSecure() {
		return cookie.isSecure();
	}

	@Override
	public void setSecure(boolean secure) {
		cookie.setSecure(secure);
	}

	@Override
	public boolean isHttpOnly() {
		return cookie.isHttpOnly();
	}

	@Override
	public void setHttpOnly(boolean httpOnly) {
		cookie.setHttpOnly(httpOnly);
	}

}
