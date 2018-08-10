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

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import mobi.f2time.dorado.rest.server.DoradoServer;
import mobi.f2time.dorado.rest.server.DoradoServerBuilder;
import mobi.f2time.dorado.spring.SpringContainer;

/**
 * @author weiping wang
 *
 */
@Configuration
@ConditionalOnClass({ EnableDorado.class })
@EnableConfigurationProperties(DoradoConfig.class)
@Order(Ordered.LOWEST_PRECEDENCE)
public class DoradoStarter {
	private static final Log LOG = LogFactory.getLog("dorado-spring-boot-starter");

	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private DoradoConfig config;

	@PostConstruct
	public void startDoradoServer() {
		if (!applicationContext.containsBean("springApplicationArguments")) {
			LOG.warn("Not SpringBootApplication, unstart dorado server");
			return;
		}
		int listenPort = config.getPort();

		if (listenPort == 0) {
			throw new IllegalArgumentException("Must be setting dorado.port property");
		}

		DoradoServerBuilder builder = DoradoServerBuilder.forPort(config.getPort());

		if (config.getAcceptors() > 0) {
			builder.acceptors(config.getAcceptors());
		}

		if (config.getIoWorkers() > 0) {
			builder.ioWorkers(config.getIoWorkers());
		}

		if (config.getMinWorkers() > 0 && config.getMaxWorkers() > 0) {
			builder.minWorkers(config.getMinWorkers()).maxWorkers(config.getMaxWorkers());
		}

		if (config.getBacklog() > 0) {
			builder.backlog(config.getBacklog());
		}

		if (config.getMaxConnections() > 0) {
			builder.maxConnection(config.getMaxConnections());
		}

		if (config.getMaxPendingRequest() > 0) {
			builder.maxPendingRequest(config.getMaxPendingRequest());
		}

		if (config.getMaxIdleTime() > 0) {
			builder.maxIdleTime(config.getMaxIdleTime());
		}

		if (config.getSendBuffer() > 0) {
			builder.sendBuffer(config.getSendBuffer());
		}

		if (config.getRecvBuffer() > 0) {
			builder.recvBuffer(config.getRecvBuffer());
		}

		if (config.getMaxPacketLength() > 0) {
			builder.maxPacketLength(config.getMaxPacketLength());
		}

		builder.scanPackages(config.getScanPackages());

		DoradoServer doradoServer = builder.build();
		SpringContainer.create(applicationContext);

		doradoServer.start();
	}
}
