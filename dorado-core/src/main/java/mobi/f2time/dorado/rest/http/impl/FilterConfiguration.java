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
package mobi.f2time.dorado.rest.http.impl;

import java.util.List;

import mobi.f2time.dorado.rest.http.Filter;
import javax.annotation.Generated;
import java.util.Collections;

/**
 * @author wangweiping
 *
 */
public class FilterConfiguration {
	private Filter filter;
	private List<String> pathPatterns;
	private List<String> excludePathPatterns;
	
	public Filter getFilter() {
		return filter;
	}
	public List<String> getPathPatterns() {
		return pathPatterns;
	}
	public List<String> getExcludePathPatterns() {
		return excludePathPatterns;
	}
	@Generated("SparkTools")
	private FilterConfiguration(Builder builder) {
		this.filter = builder.filter;
		this.pathPatterns = builder.pathPatterns;
		this.excludePathPatterns = builder.excludePathPatterns;
	}
	/**
	 * Creates builder to build {@link FilterConfiguration}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}
	/**
	 * Builder to build {@link FilterConfiguration}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private Filter filter;
		private List<String> pathPatterns = Collections.emptyList();
		private List<String> excludePathPatterns = Collections.emptyList();

		private Builder() {
		}

		public Builder withFilter(Filter filter) {
			this.filter = filter;
			return this;
		}

		public Builder withPathPatterns(List<String> pathPatterns) {
			this.pathPatterns = pathPatterns;
			return this;
		}

		public Builder withExcludePathPatterns(List<String> excludePathPatterns) {
			this.excludePathPatterns = excludePathPatterns;
			return this;
		}

		public FilterConfiguration build() {
			return new FilterConfiguration(this);
		}
	}
	
	
}
