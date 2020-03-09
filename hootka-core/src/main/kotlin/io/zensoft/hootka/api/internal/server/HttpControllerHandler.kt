package io.zensoft.hootka.api.internal.server

import io.netty.buffer.Unpooled
import io.netty.buffer.Unpooled.EMPTY_BUFFER
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.*
import io.netty.handler.codec.http.cookie.ServerCookieEncoder
import io.zensoft.hootka.api.WrappedHttpResponse
import io.zensoft.hootka.api.internal.handler.BaseRequestProcessor
import io.zensoft.hootka.api.internal.http.NettyWrappedHttpRequest
import io.zensoft.hootka.api.internal.http.NettyWrappedHttpResponse
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

@ChannelHandler.Sharable
class HttpControllerHandler(
    private val requestProcessor: BaseRequestProcessor
) : SimpleChannelInboundHandler<FullHttpRequest>() {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun channelRead0(ctx: ChannelHandlerContext, request: FullHttpRequest) {
        val socketAddress = ctx.channel().remoteAddress() as InetSocketAddress
        val wrappedResponse = NettyWrappedHttpResponse()
        val wrappedRequest = NettyWrappedHttpRequest.create(request, socketAddress.address.hostAddress)
        requestProcessor.processRequest(wrappedRequest, wrappedResponse)
        writeResponse(ctx, request, wrappedResponse)
    }

    /**
     * Marked deprecated as method moved from [io.netty.channel.ChannelHandler] to it's child interface [io.netty.channel.ChannelInboundHandler]
     */
    @Throws(Exception::class)
    @Suppress("OverridingDeprecatedMember")
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error("Something went wrong", cause)
        ctx.close()
    }

    private fun writeResponse(ctx: ChannelHandlerContext, request: FullHttpRequest?, response: NettyWrappedHttpResponse) {
        val content = response.getContent()
        val result = if (null != content) {
            val buf = Unpooled.wrappedBuffer(content)
            DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.getHttpStatus().code), buf)
        } else {
            DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.getHttpStatus().code))
        }
        response.getHeaders().forEach { header ->
            header.value.forEach {
                result.headers().add(header.key, it)
            }
        }
        result.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(response.getCookies()))
        if (result.content() != EMPTY_BUFFER) {
            result.headers().add(HttpHeaderNames.CONTENT_TYPE, response.getContentType().value.toString() + "; charset=UTF-8")
        }
        result.headers().set(HttpHeaderNames.CONTENT_LENGTH, result.content()!!.readableBytes())

        if (HttpUtil.isKeepAlive(request)) {
            HttpUtil.setKeepAlive(result.headers(), HttpVersion.HTTP_1_1, true)
        }

        ctx.writeAndFlush(result, ctx.voidPromise())
    }

}