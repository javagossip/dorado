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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
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
import mobi.f2time.dorado.hotswap.DoradoClassLoader;
import mobi.f2time.dorado.rest.http.impl.Webapp;
import mobi.f2time.dorado.rest.util.ClassLoaderUtils;
import mobi.f2time.dorado.rest.util.Constant;
import mobi.f2time.dorado.rest.util.IOUtils;

/**
 * 
 * @author wangwp
 */
public class DoradoServer {
	private static final Logger LOG = LoggerFactory.getLogger(Constant.SERVER_NAME);

	private final DoradoServerBuilder builder;

	public DoradoServer(DoradoServerBuilder builder) {
		this.builder = builder;
	}

	public void start() {
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
			bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

			ChannelFuture f = bootstrap.bind(builder.getPort()).sync();

			// print dorado ascii-art logo,use figlet generate ascii-art logo
			String doradoAscii = IOUtils.toString(ClassLoaderUtils.getStream("dorado-ascii"));
			System.out.println(doradoAscii);
			System.out.println();

			Webapp.create(builder.scanPackages());
			if (builder.isDevMode()) {
				reloadWebappIfNeed();
			}
			LOG.info(String.format("Dorado application initialized with port(s): %d (http)", builder.getPort()));
			f.channel().closeFuture().sync();
		} catch (Throwable ex) {
			LOG.error("start dorado server failed, cause: ", ex);
		} finally {
			worker.shutdownGracefully();
			acceptor.shutdownGracefully();
		}
	}

	private void reloadWebappIfNeed() {
		final String watchingClasspath = ClassLoaderUtils.getPath("");
		// 启动一个监控类文件变化的线程，如果类文件发现变化则重新加载
		new Thread(() -> {
			try {
				reloadClassesIfNeed(watchingClasspath);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}).start();
	}

	private void reloadClassesIfNeed(String classpath) throws Exception {
		WatchService classFilesWatcher = FileSystems.getDefault().newWatchService();
		Path rootPath = Paths.get(classpath);
		rootPath.register(classFilesWatcher, StandardWatchEventKinds.ENTRY_MODIFY);

		List<Path> allWatchDirs = recurseListFiles(rootPath);
		for (Path watchDir : allWatchDirs) {
			watchDir.register(classFilesWatcher, StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
		}

		while (!Thread.currentThread().isInterrupted()) {
			try {
				WatchKey watchKey = classFilesWatcher.poll(10, TimeUnit.MILLISECONDS);
				if (watchKey == null)
					continue;

				final AtomicBoolean isNeedReload = new AtomicBoolean(false);

				List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
				watchEvents.stream().forEach(event -> {
					Path watchedPath = (Path) event.context();
					try {
						LOG.info("File {} changed in classpath, reload webapp", watchedPath.toString());
						isNeedReload.compareAndSet(false, true);
					} catch (Exception ex) {
						LOG.error("watching file changed error", ex);
					}
				});
				watchKey.reset();
				if (isNeedReload.get()) {
					Dorado.classLoader = new DoradoClassLoader();
					Webapp.get().reload();
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	private static List<Path> recurseListFiles(Path root) throws IOException {
		List<Path> results = new ArrayList<>();
		List<Path> pathList = Files.list(root).filter(p -> p.toFile().isDirectory()).collect(Collectors.toList());

		for (Path p : pathList) {
			results.add(p);
			results.addAll(recurseListFiles(p));
		}
		return results;
	}
}
