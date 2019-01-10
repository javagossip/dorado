/*
 * Copyright 2014-2018 f2time.com All right reserved.
 */
package ai.houyi.dorado.example.filter;

import ai.houyi.dorado.rest.annotation.FilterPath;
import ai.houyi.dorado.rest.http.Filter;
import ai.houyi.dorado.rest.http.HttpRequest;
import ai.houyi.dorado.rest.http.HttpResponse;

/**
 * @author weiping wang
 *
 */
public class DemoFilter implements Filter {

	@Override
	public boolean preFilter(HttpRequest request, HttpResponse response) {
		System.out.println("execute demo filter");
		response.sendError(403, "Forbidden");
		return false;
	}
}
