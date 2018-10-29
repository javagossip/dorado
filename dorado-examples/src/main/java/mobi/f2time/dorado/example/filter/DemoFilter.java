/*
 * Copyright 2014-2018 f2time.com All right reserved.
 */
package mobi.f2time.dorado.example.filter;

import mobi.f2time.dorado.rest.annotation.FilterPath;
import mobi.f2time.dorado.rest.http.Filter;
import mobi.f2time.dorado.rest.http.HttpRequest;
import mobi.f2time.dorado.rest.http.HttpResponse;

/**
 * @author weiping wang
 *
 */
@FilterPath(include = "/campaign/*")
public class DemoFilter implements Filter {

	@Override
	public boolean preFilter(HttpRequest request, HttpResponse response) {
		System.out.println("execute demo filter");
		response.sendError(403, "Forbidden");
		return false;
	}
}
