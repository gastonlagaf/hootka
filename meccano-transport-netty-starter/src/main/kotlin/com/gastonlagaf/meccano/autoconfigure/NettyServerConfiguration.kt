package com.gastonlagaf.meccano.autoconfigure

import com.gastonlagaf.meccano.api.HttpServer
import com.gastonlagaf.meccano.api.RequestProcessor
import com.gastonlagaf.meccano.transport.netty.HttpChannelInitializer
import com.gastonlagaf.meccano.transport.netty.HttpControllerHandler
import com.gastonlagaf.meccano.transport.netty.NettyServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NettyServerConfiguration(
    private val requestProcessor: RequestProcessor,
    @Value("\${meccano.port:8080}") private val port: Int
) {

    @Bean
    @ConditionalOnMissingBean(HttpControllerHandler::class)
    fun httpControllerHandler(): HttpControllerHandler = HttpControllerHandler(requestProcessor)

    @Bean
    @ConditionalOnMissingBean(HttpChannelInitializer::class)
    fun httpChannelInitializer(): HttpChannelInitializer = HttpChannelInitializer(httpControllerHandler())

    @Bean
    @ConditionalOnMissingBean(HttpServer::class)
    fun nettyHttpServer(): HttpServer = NettyServer(port, httpChannelInitializer()).also { it.startup() }

}