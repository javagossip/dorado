/*
 * Copyright 2014-2018 f2time.com All right reserved.
 */
package ai.houyi.dorado.example.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import ai.houyi.dorado.springboot.DoradoSpringBootApplication;
import ai.houyi.dorado.swagger.EnableSwagger;

/**
 * @author wangweiping
 *
 */
@DoradoSpringBootApplication
@EnableSwagger
@ComponentScan({"ai.houyi.dorado.example"})
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}
