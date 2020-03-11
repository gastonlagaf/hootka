package io.zensoft.hootka.api.internal.server.netty

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec

class HttpChannelInitializer(
    private val httpControllerHandler: HttpControllerHandler
) : ChannelInitializer<SocketChannel>() {

    override fun initChannel(ch: SocketChannel) {
        ch.pipeline()
            .addLast(HttpServerCodec())
            .addLast(HttpObjectAggregator(20971520)) //1048576
            .addLast(httpControllerHandler)
    }

}