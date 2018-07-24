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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import mobi.f2time.dorado.rest.servlet.FilterChain;

/**
 * 
 * @author wangwp
 */
public class FilterManager {
	private static final String EXCLUDE_URL_PATTERN = "^//*$";
	
	private List<FilterConfiguration> doradoFilters;
	private Pattern excludePattern;

	private static final FilterManager INSTANCE = new FilterManager();

	private FilterManager() {
		this.doradoFilters = new ArrayList<>();
		excludePattern = Pattern.compile(EXCLUDE_URL_PATTERN);
	}

	public static FilterManager getInstance() {
		return INSTANCE;
	}

	public void addFilter(FilterConfiguration filter) {
		doradoFilters.add(filter);
	}

	public FilterChain filter(String uri) {
		FilterChainImpl filterChain = new FilterChainImpl();
		if (excludePattern.matcher(uri).matches()) {
			return filterChain;
		}
		
		doradoFilters.stream().filter(filter -> filter.getUrlPattern().matcher(uri).matches())
				.forEachOrdered(filter -> filterChain.addFilter(filter.getFilter()));

		return filterChain;
	}
}
