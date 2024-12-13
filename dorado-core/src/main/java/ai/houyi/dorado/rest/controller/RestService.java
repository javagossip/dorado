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
package ai.houyi.dorado.rest.controller;
/**
 * 
 * @author wangwp
 */
public class RestService {
	private String path;
	private String method;
	private String desc;

	public RestService() {
	}

	private RestService(Builder builder) {
		this.path = builder.path;
		this.method = builder.method;
		this.desc = builder.desc;
	}

	/**
	 * Creates builder to build {@link RestService}.
	 * 
	 * @return created builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	public String getPath() {
		return path;
	}

	public String getMethod() {
		return method;
	}

	public String getDesc() {
		return desc;
	}

	/**
	 * Builder to build {@link RestService}.
	 */
	public static final class Builder {
		private String path;
		private String method;
		private String desc;

		private Builder() {
		}

		public Builder withPath(String path) {
			this.path = path;
			return this;
		}

		public Builder withMethod(String method) {
			this.method = method;
			return this;
		}

		public Builder withDesc(String desc) {
			this.desc = desc;
			return this;
		}

		public RestService build() {
			return new RestService(this);
		}
	}
}
