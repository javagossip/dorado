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
package mobi.f2time.dorado.rest.servlet;

/**
 * 
 * @author wangwp
 */
public interface Cookie {

	long UNDEFINED_MAX_AGE = Long.MIN_VALUE;

	String name();

	String value();

	void setValue(String value);

	boolean wrap();

	void setWrap(boolean wrap);

	String domain();

	void setDomain(String domain);

	String path();

	void setPath(String path);

	long maxAge();

	void setMaxAge(long maxAge);

	boolean isSecure();

	void setSecure(boolean secure);

	boolean isHttpOnly();

	void setHttpOnly(boolean httpOnly);
}
