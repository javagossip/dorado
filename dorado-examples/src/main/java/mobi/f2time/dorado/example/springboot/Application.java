/*
 * Copyright 2014-2018 f2time.com All right reserved.
 */
package mobi.f2time.dorado.example.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import mobi.f2time.dorado.springboot.EnableDorado;
import mobi.f2time.dorado.swagger.EnableSwagger;

/**
 * @author wangweiping
 *
 */
@EnableDorado
@SpringBootApplication
@EnableSwagger
@ComponentScan({"mobi.f2time.dorado.example.controller"})
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}
