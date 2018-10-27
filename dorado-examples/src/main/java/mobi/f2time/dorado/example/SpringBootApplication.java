/*
 * Copyright 2014-2018 f2time.com All right reserved.
 */
package mobi.f2time.dorado.example;

import org.springframework.boot.SpringApplication;

import mobi.f2time.dorado.springboot.EnableDorado;
import mobi.f2time.dorado.swagger.EnableSwagger;

/**
 * @author wangweiping
 *
 */
@EnableDorado
@org.springframework.boot.autoconfigure.SpringBootApplication
@EnableSwagger
public class SpringBootApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringBootApplication.class, args);
	}
}
