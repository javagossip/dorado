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

import java.io.IOException;
import java.util.LinkedList;

import mobi.f2time.dorado.rest.servlet.Filter;
import mobi.f2time.dorado.rest.servlet.FilterChain;
import mobi.f2time.dorado.rest.servlet.HttpRequest;
import mobi.f2time.dorado.rest.servlet.HttpResponse;

/**
 * 
 * @author wangwp
 */
public class FilterChainImpl implements FilterChain {
	private LinkedList<Filter> filters = new LinkedList<>();

	public void addFilter(Filter filter) {
		filters.add(filter);
	}

	@Override
	public void doFilter(HttpRequest request, HttpResponse response) throws IOException {
		if (filters.isEmpty()) {
			return;
		}

		try {
			filters.poll().doFilter(request, response, this);
		} catch (IOException ex) {
			throw ex;
		}
	}
}
