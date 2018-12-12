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
package ai.houyi.dorado.example.model;

import javax.annotation.Generated;

/**
 * 
 * @author wangwp
 */
public class TestResp {
	private int code;
	private String msg;

	private Object data;

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public Object getData() {
		return data;
	}

	@Generated("SparkTools")
	private TestResp(Builder builder) {
		this.code = builder.code;
		this.msg = builder.msg;
		this.data = builder.data;
	}

	public TestResp() {
	}

	/**
	 * Creates builder to build {@link TestResp}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link TestResp}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private int code;
		private String msg;
		private Object data;

		private Builder() {
		}

		public Builder withCode(int code) {
			this.code = code;
			return this;
		}

		public Builder withMsg(String msg) {
			this.msg = msg;
			return this;
		}

		public Builder withData(Object data) {
			this.data = data;
			return this;
		}

		public TestResp build() {
			return new TestResp(this);
		}
	}

}
