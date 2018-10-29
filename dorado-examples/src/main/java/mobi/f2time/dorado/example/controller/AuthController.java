/*
 * Copyright 2014-2018 f2time.com All right reserved.
 */
package mobi.f2time.dorado.example.controller;

import mobi.f2time.dorado.rest.annotation.Controller;
import mobi.f2time.dorado.rest.annotation.GET;
import mobi.f2time.dorado.rest.annotation.Path;

/**
 * @author weiping wang
 *
 */
@Controller
@Path("/auth")
public class AuthController {
	
	@Path("/signin")
	@GET
	public String signin(String name,String pwd) {
		System.out.println("signin name: "+name);
		System.out.println("signin pwd: "+pwd);
		
		return "signin success";
	}
}
