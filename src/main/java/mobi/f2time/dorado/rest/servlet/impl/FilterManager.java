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

import mobi.f2time.dorado.rest.servlet.FilterChain;

/**
 * 
 * @author wangwp
 */
public class FilterManager {
	private List<FilterConfiguration> doradoFilters;

	private static final FilterManager INSTANCE = new FilterManager();

	private FilterManager() {
		this.doradoFilters = new ArrayList<>();
	}

	public static FilterManager getInstance() {
		return INSTANCE;
	}

	public void addFilter(FilterConfiguration filter) {
		doradoFilters.add(filter);
	}

	public FilterChain filter(String uri) {
		FilterChainImpl filterChain = new FilterChainImpl();
		doradoFilters.stream().filter(filter -> filter.getUrlPattern().matcher(uri).matches())
				.forEachOrdered(filter -> filterChain.addFilter(filter.getFilter()));

		return filterChain;
	}
}
