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

import static mobi.f2time.dorado.rest.http.impl.ChannelHolder.*;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.timeout.IdleStateEvent;
import mobi.f2time.dorado.Dorado;
import mobi.f2time.dorado.rest.controller.DoradoStatus;
import mobi.f2time.dorado.rest.http.FilterChain;
import mobi.f2time.dorado.rest.http.HttpRequest;
import mobi.f2time.dorado.rest.http.HttpResponse;
import mobi.f2time.dorado.rest.http.impl.FilterManager;
import mobi.f2time.dorado.rest.http.impl.HttpRequestImpl;
import mobi.f2time.dorado.rest.http.impl.HttpResponseImpl;
import mobi.f2time.dorado.rest.http.impl.Webapp;
import mobi.f2time.dorado.rest.router.UriRoutingMatchResult;
import mobi.f2time.dorado.rest.util.LogUtils;
import mobi.f2time.dorado.rest.util.TracingThreadPoolExecutor;

/**
 * 
 * @author wangwp
 */
public class DoradoServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	private final TracingThreadPoolExecutor asyncExecutor;
	private final Webapp webapp;
	private final DoradoStatus status;
	private final boolean isDevMode;

	private DoradoServerHandler(DoradoServerBuilder builder) {
		this.webapp = Webapp.get();
		this.asyncExecutor = (TracingThreadPoolExecutor) builder.executor();
		this.status = DoradoStatus.get();
		this.isDevMode = builder.isDevMode();
	}

	public static DoradoServerHandler create(DoradoServerBuilder builder) {
		return new DoradoServerHandler(builder);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		status.totalRequestsIncrement();

		if (asyncExecutor == null) {
			handleHttpRequest(ctx, msg);
			return;
		}

		asyncExecutor.execute(() -> {
			handleHttpRequest(ctx, msg);
		});
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, Object msg) {
		if (isDevMode) {
			Thread.currentThread().setContextClassLoader(Dorado.classLoader);
		}

		FullHttpRequest request = (FullHttpRequest) msg;
		FullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);

		boolean isKeepAlive = HttpUtil.isKeepAlive(request);
		HttpUtil.setKeepAlive(response, isKeepAlive);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
		response.headers().set(HttpHeaderNames.SERVER, "dorado_1.x");

		ChannelFuture channelFuture = null;
		try {
			set(ctx.channel());
			HttpRequest _request = new HttpRequestImpl(request);
			HttpResponse _response = new HttpResponseImpl(response);

			UriRoutingMatchResult uriRouting = webapp.getUriRoutingRegistry().findRouteController(_request);
			if (uriRouting == null) {
				response.setStatus(HttpResponseStatus.NOT_FOUND);
				ByteBufUtil.writeUtf8(response.content(), String.format(
						"Resource not found, url: [%s], http_method: [%s]", request.uri(), _request.getMethod()));
			} else {
				FilterChain filterChain = FilterManager.getInstance().filter(_request.getRequestURI());
				filterChain.doFilter(_request, _response);

				String[] pathVariables = uriRouting.pathVariables();
				uriRouting.controller().invoke(_request, _response, pathVariables);
			}
		} catch (Throwable ex) {
			LogUtils.error("handle http request error", ex);
			response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
			ByteBufUtil.writeUtf8(response.content(), "500 Internal Server Error");
		} finally {
			unset();
			if (isKeepAlive) {
				HttpUtil.setContentLength(response, response.content().readableBytes());
			}
			channelFuture = ctx.channel().writeAndFlush(response);
			if (!isKeepAlive && channelFuture != null) {
				channelFuture.addListener(ChannelFutureListener.CLOSE);
			}
			status.handledRequestsIncrement();
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
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		status.connectionIncrement();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		status.connectionDecrement();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LogUtils.error(cause.getMessage(), cause);
		ctx.channel().close();
	}
}
