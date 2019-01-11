/*
 * Copyright 2014-2018 f2time.com All right reserved.
 */
package ai.houyi.dorado.example;

import ai.houyi.dorado.rest.server.DoradoServer;
import ai.houyi.dorado.rest.server.DoradoServerBuilder;
import ai.houyi.dorado.swagger.EnableSwagger;

/**
 * @author wangweiping
 *
 */
@EnableSwagger
public class Application {
	public static void main(String[] args) throws Exception {
		DoradoServer server = DoradoServerBuilder.forPort(18889).build();
		//Webapp.get().getMethodReturnValueHandlerConfig().addExcludePath("/file/upload/*");
		
		server.start();
	}
}
