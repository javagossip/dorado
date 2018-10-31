/*
 * Copyright 2014-2018 f2time.com All right reserved.
 */
package ai.houyi.dorado.example.controller;

import ai.houyi.dorado.rest.annotation.Controller;
import ai.houyi.dorado.rest.annotation.GET;
import ai.houyi.dorado.rest.annotation.Path;

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
