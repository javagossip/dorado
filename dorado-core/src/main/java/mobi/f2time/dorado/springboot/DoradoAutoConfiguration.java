/*
 * Copyright 2017 The OpenDSP Project
 *
 * The OpenDSP Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package mobi.f2time.dorado.springboot;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import mobi.f2time.dorado.rest.server.DoradoServer;
import mobi.f2time.dorado.rest.server.DoradoServerBuilder;
import mobi.f2time.dorado.spring.SpringContainer;

/**
 * @author weiping wang
 *
 */
@Configuration
@ConditionalOnBean(annotation = EnableDorado.class)
@EnableConfigurationProperties(DoradoConfig.class)
@Order(Ordered.LOWEST_PRECEDENCE)
public class DoradoAutoConfiguration {
	static final Log LOG = LogFactory.getLog("dorado-spring-boot-starter");

	@Autowired
	private DoradoConfig config;

	@Autowired
	private ApplicationContext applicationContext;

	@PostConstruct
	public void startDoradoServer() {
		boolean isSpringBootApp = applicationContext.containsBean("springApplicationArguments");

		if (!isSpringBootApp) {
			LOG.info("Not SpringBoot Application launch, unstart dorado server!");
			return;
		}

		DoradoServerBuilder builder = DoradoServerBuilder.forPort(config.getPort()).backlog(config.getBacklog())
				.acceptors(config.getAcceptors()).ioWorkers(config.getIoWorkers()).minWorkers(config.getMinWorkers())
				.maxWorkers(config.getMaxWorkers()).maxConnection(config.getMaxConnections())
				.maxPendingRequest(config.getMaxPendingRequest()).maxIdleTime(config.getMaxIdleTime())
				.sendBuffer(config.getSendBuffer()).recvBuffer(config.getRecvBuffer())
				.maxPacketLength(config.getMaxPacketLength()).contextPath(config.getContextPath());

		String[] scanPackages = config.getScanPackages();
		if (config.getScanPackages() == null || config.getScanPackages().length == 0) {
			scanPackages = getSpringBootAppScanPackages();
		}

		DoradoServer doradoServer = builder.scanPackages(scanPackages).build();
		SpringContainer.create(applicationContext);
		new Thread(() -> doradoServer.start()).start();
	}

	private String[] getSpringBootAppScanPackages() {
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext;

		Set<String> packages = new HashSet<>();
		String[] names = registry.getBeanDefinitionNames();
		for (String name : names) {
			BeanDefinition definition = registry.getBeanDefinition(name);
			if (definition instanceof AnnotatedBeanDefinition) {
				AnnotatedBeanDefinition annotatedDefinition = (AnnotatedBeanDefinition) definition;
				addComponentScanningPackages(packages, annotatedDefinition.getMetadata());
			}
		}
		return packages.toArray(new String[] {});
	}

	private void addComponentScanningPackages(Set<String> packages, AnnotationMetadata metadata) {
		AnnotationAttributes attributes = AnnotationAttributes
				.fromMap(metadata.getAnnotationAttributes(ComponentScan.class.getName(), true));
		if (attributes != null) {
			addPackages(packages, attributes.getStringArray("value"));
			addPackages(packages, attributes.getStringArray("basePackages"));
			addClasses(packages, attributes.getStringArray("basePackageClasses"));
			if (packages.isEmpty()) {
				packages.add(ClassUtils.getPackageName(metadata.getClassName()));
			}
		}
	}

	private void addPackages(Set<String> packages, String[] values) {
		if (values != null) {
			Collections.addAll(packages, values);
		}
	}

	private void addClasses(Set<String> packages, String[] values) {
		if (values != null) {
			for (String value : values) {
				packages.add(ClassUtils.getPackageName(value));
			}
		}
	}
}
