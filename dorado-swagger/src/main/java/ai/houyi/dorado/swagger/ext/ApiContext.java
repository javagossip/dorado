/*
 * Copyright 2017-2019 The OpenAds Project
 *
 * The OpenAds Project licenses this file to you under the Apache License,
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
package ai.houyi.dorado.swagger.ext;

import io.swagger.models.Info;
import javax.annotation.Generated;

/**
 * @author weiping wang
 */
public class ApiContext {
	private Info info;
	private ApiKey apiKey;

	@Generated("SparkTools")
	private ApiContext(Builder builder) {
		this.info = builder.info;
		this.apiKey = builder.apiKey;
	}
	
	public Info getInfo() {
		return info;
	}
	
	public ApiKey getApiKey() {
		return apiKey;
	}
	
	/**
	 * Creates builder to build {@link ApiContext}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}
	/**
	 * Builder to build {@link ApiContext}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private Info info;
		private ApiKey apiKey;

		private Builder() {
		}

		public Builder withInfo(Info info) {
			this.info = info;
			return this;
		}

		public Builder withApiKey(ApiKey apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public ApiContext build() {
			return new ApiContext(this);
		}
	}
}
