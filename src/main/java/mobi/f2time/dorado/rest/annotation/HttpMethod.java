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
package mobi.f2time.dorado.rest.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author wangwp
 */
@Documented
@Retention(RUNTIME)
@Target({ METHOD, ANNOTATION_TYPE })
public @interface HttpMethod {
	/**
	 * HTTP GET method
	 */
	public static final String GET = "GET";
	/**
	 * HTTP POST method
	 */
	public static final String POST = "POST";
	/**
	 * HTTP PUT method
	 */
	public static final String PUT = "PUT";
	/**
	 * HTTP DELETE method
	 */
	public static final String DELETE = "DELETE";
	/**
	 * HTTP HEAD method
	 */
	public static final String HEAD = "HEAD";
	/**
	 * HTTP OPTIONS method
	 */
	public static final String OPTIONS = "OPTIONS";

	/**
	 * Specifies the name of a HTTP method. E.g. "GET".
	 */
	String value();
}
