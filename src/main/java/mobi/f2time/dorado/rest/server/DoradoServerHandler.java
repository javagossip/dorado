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

import static mobi.f2time.dorado.rest.servlet.impl.ChannelHolder.set;
import static mobi.f2time.dorado.rest.servlet.impl.ChannelHolder.unset;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.timeout.IdleStateEvent;
import mobi.f2time.dorado.rest.router.UriRoutingMatchResult;
import mobi.f2time.dorado.rest.servlet.FilterChain;
import mobi.f2time.dorado.rest.servlet.HttpRequest;
import mobi.f2time.dorado.rest.servlet.HttpResponse;
import mobi.f2time.dorado.rest.servlet.impl.FilterManager;
import mobi.f2time.dorado.rest.servlet.impl.HttpRequestImpl;
import mobi.f2time.dorado.rest.servlet.impl.HttpResponseImpl;
import mobi.f2time.dorado.rest.servlet.impl.Webapp;

/**
 * 
 * @author wangwp
 */
public class DoradoServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(DoradoServerHandler.class);

	private final ExecutorService asyncExecutor;
	private final Webapp webapp;

	private DoradoServerHandler(DoradoServerBuilder builder) {
		this.webapp = Webapp.get();
		asyncExecutor = builder.executor();
	}

	public static DoradoServerHandler create(DoradoServerBuilder builder) {
		return new DoradoServerHandler(builder);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (asyncExecutor == null) {
			handleHttpRequest(ctx, msg);
			return;
		}
		
		asyncExecutor.execute(() -> {
			handleHttpRequest(ctx, msg);
		});
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, Object msg) {
		FullHttpRequest request = (FullHttpRequest) msg;
		FullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);

		boolean isKeepAlive = HttpUtil.isKeepAlive(request);
		HttpUtil.setKeepAlive(response, isKeepAlive);
		response.headers().set(HttpHeaderNames.SERVER, "dorado_1.x");

		ChannelFuture channelFuture = null;
		try {
			set(ctx.channel());
			HttpRequest _request = new HttpRequestImpl(request);
			HttpResponse _response = new HttpResponseImpl(response);

			UriRoutingMatchResult uriRouting = webapp.getUriRoutingRegistry().findRouteController(_request);
			if (uriRouting == null) {
				response.setStatus(HttpResponseStatus.NOT_FOUND);
				ByteBufUtil.writeUtf8(response.content(), String.format("resource not found,url: %s, http_method:%s",
						request.uri(), _request.getMethod()));
			} else {
				FilterChain filterChain = FilterManager.getInstance().filter(_request.getRequestURI());
				filterChain.doFilter(_request, _response);

				String[] pathVariables = uriRouting.pathVariables();
				try {
					uriRouting.controller().invoke(_request, _response, pathVariables);
				} catch (Exception ex) {
					response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
					ByteBufUtil.writeUtf8(response.content(), "500 Internal Server Error");
					throw ex;
				}
			}

			if (isKeepAlive) {
				HttpUtil.setContentLength(response, response.content().readableBytes());
			}
			channelFuture = ctx.channel().writeAndFlush(response);
		} catch (Throwable ex) {
			LOG.error("handle http request error", ex);
		} finally {
			unset();
			if (!isKeepAlive && channelFuture != null) {
				channelFuture.addListener(ChannelFutureListener.CLOSE);
			}
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e == IdleStateEvent.READER_IDLE_STATE_EVENT) {
				ctx.channel().close();
			}
		}
		super.userEventTriggered(ctx, evt);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOG.error(cause.getMessage(), cause);
		ctx.channel().close();
	}

}
