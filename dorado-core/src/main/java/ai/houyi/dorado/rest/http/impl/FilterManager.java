/*
 * Copyright 2014-2018 f2time.com All right reserved.
 */
package ai.houyi.dorado.rest.http.impl;

import java.util.ArrayList;
import java.util.List;

import ai.houyi.dorado.rest.http.Filter;
import ai.houyi.dorado.rest.util.AntPathMatcher;

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

			// 如果在排除路径中忽略
			if (excludePathPatterns != null && !excludePathPatterns.isEmpty()) {
				for (String excluePathPattern : excludePathPatterns) {
					if (pathMatcher.match(excluePathPattern, uri)) {
						continue filterLoop;
					}
				}
			}
			// 如果只有排除路径没有include路径的话直接加入此过滤器
			if (pathPatterns == null || pathPatterns.isEmpty()) {
				filters.add(config.getFilter());
				continue filterLoop;
			}
			// 如果存在include路径，则再判断是否匹配include路径，匹配则加入
			for (String pathPattern : pathPatterns) {
				if (pathMatcher.match(pathPattern, uri)) {
					filters.add(config.getFilter());
				}
			}
		}
		return filters;
	}
}
