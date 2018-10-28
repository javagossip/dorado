/*
 * Copyright 2014-2018 f2time.com All right reserved.
 */
package mobi.f2time.dorado.rest.http.impl;

import java.util.ArrayList;
import java.util.List;

import mobi.f2time.dorado.rest.http.Filter;
import mobi.f2time.dorado.rest.util.AntPathMatcher;

/**
 * @author wangweiping
 *
 */
public class FilterManager {
	private static final FilterManager instance = new FilterManager();

	private final List<FilterConfiguration> filterConfigurations;
	private final AntPathMatcher pathMatcher;

	private FilterManager() {
		this.filterConfigurations = new ArrayList<>();
		this.pathMatcher = new AntPathMatcher();
	}

	public static FilterManager getInstance() {
		return instance;
	}

	public void addFilterConfiguration(FilterConfiguration filterConfiguration) {
		this.filterConfigurations.add(filterConfiguration);
	}

	public List<Filter> match(String uri) {
		List<Filter> filters = new ArrayList<>();

		filterLoop: for (FilterConfiguration config : filterConfigurations) {
			List<String> excludePathPatterns = config.getExcludePathPatterns();
			List<String> pathPatterns = config.getPathPatterns();

			if (excludePathPatterns != null) {
				for (String excluePathPattern : excludePathPatterns) {
					if (pathMatcher.match(excluePathPattern, uri)) {
						continue filterLoop;
					}
				}
			}

			if (pathPatterns != null) {
				for (String pathPattern : pathPatterns) {
					if (pathMatcher.match(pathPattern, uri)) {
						filters.add(config.getFilter());
					}
				}
			}
		}
		return filters;
	}
}
