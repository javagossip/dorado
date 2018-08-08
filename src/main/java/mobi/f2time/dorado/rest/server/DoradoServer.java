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
package mobi.f2time.dorado.rest.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import mobi.f2time.dorado.rest.http.impl.Webapp;
import mobi.f2time.dorado.rest.util.ClassLoaderUtils;
import mobi.f2time.dorado.rest.util.LogUtils;
import mobi.f2time.dorado.spring.DoradoApplicationContext;

/**
 * 
 * @author wangwp
 */
public class DoradoServer {

	private final DoradoServerBuilder builder;

	public DoradoServer(DoradoServerBuilder builder) {
		this.builder = builder;
	}

	public void start() {
		// print dorado ascii-art logo,use figlet generate ascii-art logo
		System.out.println(ClassLoaderUtils.getResoureAsString("dorado-ascii"));
		System.out.println();

		if (builder.isEnableSpring()) {
			final DoradoApplicationContext ctx = new DoradoApplicationContext(builder.scanPackages());
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				ctx.close();
			}));
		}

		Webapp.create(builder.scanPackages(), builder.isDevMode());

		EventLoopGroup acceptor = new NioEventLoopGroup(builder.getAcceptors());
		EventLoopGroup worker = new NioEventLoopGroup(builder.getIoWorkers());

		ServerBootstrap bootstrap = null;
		try {
			bootstrap = new ServerBootstrap().group(acceptor, worker).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<Channel>() {
						@Override
						protected void initChannel(Channel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();

							pipeline.addLast(new HttpServerCodec());
							pipeline.addLast(new HttpObjectAggregator(builder.getMaxPacketLength()));
							pipeline.addLast(new IdleStateHandler(builder.getMaxIdleTime(), 0, 0));
							pipeline.addLast(DoradoServerHandler.create(builder));
						}
					});

			bootstrap.option(ChannelOption.SO_BACKLOG, builder.getBacklog());
			bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
			bootstrap.childOption(ChannelOption.SO_SNDBUF, builder.getSendBuffer());
			bootstrap.childOption(ChannelOption.SO_RCVBUF, builder.getRecvBuffer());

			ChannelFuture f = bootstrap.bind(builder.getPort()).sync();
			LogUtils.info(String.format("Dorado application initialized with port: %d (http)", builder.getPort()));
			f.channel().closeFuture().sync();
		} catch (Throwable ex) {
			LogUtils.error("Start dorado application failed, cause: " + ex.getMessage(), ex);
		} finally {
			worker.shutdownGracefully();
			acceptor.shutdownGracefully();
		}
	}
}
