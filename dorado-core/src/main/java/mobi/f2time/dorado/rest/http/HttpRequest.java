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
package mobi.f2time.dorado.rest.http;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author wangwp
 */
public interface HttpRequest {
	String getParameter(String name);

	String[] getParameterValues(String name);

	Map<String, List<String>> getParameters();

	String getRemoteAddr();

	Cookie[] getCookies();

	String getHeader(String name);

	String[] getHeaders(String name);

	String getMethod();

	String getQueryString();

	String getRequestURI();

	InputStream getInputStream();
	
	MultipartFile getFile();
	
	MultipartFile[] getFiles();
}
