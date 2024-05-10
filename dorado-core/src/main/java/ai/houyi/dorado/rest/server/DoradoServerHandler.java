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
package ai.houyi.dorado.rest.server;

import ai.houyi.dorado.rest.controller.DoradoStatus;
import ai.houyi.dorado.rest.http.HttpRequest;
import ai.houyi.dorado.rest.http.HttpResponse;
import ai.houyi.dorado.rest.http.impl.DoradoHttpRequest;
import ai.houyi.dorado.rest.http.impl.DoradoHttpResponse;
import ai.houyi.dorado.rest.http.impl.Webapp;
import ai.houyi.dorado.rest.router.Router;
import ai.houyi.dorado.rest.util.ExceptionUtils;
import ai.houyi.dorado.rest.util.LogUtils;
import ai.houyi.dorado.rest.util.TracingThreadPoolExecutor;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;

import static ai.houyi.dorado.rest.http.impl.ChannelHolder.set;
import static ai.houyi.dorado.rest.http.impl.ChannelHolder.unset;

/**
 * @author wangwp
 */
public class DoradoServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final TracingThreadPoolExecutor asyncExecutor;
    private final Webapp webapp;
    private final DoradoStatus status;

    private DoradoServerHandler(DoradoServerBuilder builder) {
        this.webapp = Webapp.get();
        this.asyncExecutor = builder.executor();
        this.status = DoradoStatus.get();
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
        asyncExecutor.execute(() -> handleHttpRequest(ctx, msg));
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        FullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);

        boolean isKeepAlive = HttpUtil.isKeepAlive(request);
        HttpUtil.setKeepAlive(response, isKeepAlive);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        response.headers().set(HttpHeaderNames.SERVER, "Dorado");

        ChannelFuture channelFuture;
        try {
            set(ctx.channel());
            HttpRequest _request = new DoradoHttpRequest(request);
            HttpResponse _response = new DoradoHttpResponse(response);

            Router router = webapp.getUriRoutingRegistry().findRouteController(_request);
            if (router == null) {
                response.setStatus(HttpResponseStatus.NOT_FOUND);
                ByteBufUtil.writeUtf8(response.content(),
                        String.format("Resource not found, url: [%s], http_method: [%s]", _request.getRequestURI(),
                                _request.getMethod()));
            } else {
                router.invoke(_request, _response);
            }
        } catch (Throwable ex) {
            LogUtils.error("handle http request error", ex);
            response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            ByteBufUtil.writeUtf8(response.content(), ExceptionUtils.toString(ex));
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
