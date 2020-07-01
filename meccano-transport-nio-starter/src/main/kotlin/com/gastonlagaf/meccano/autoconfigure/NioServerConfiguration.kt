package com.gastonlagaf.meccano.autoconfigure

import com.gastonlagaf.meccano.api.HttpServer
import com.gastonlagaf.meccano.api.RequestProcessor
import com.gastonlagaf.meccano.transport.nio.NioServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NioServerConfiguration(
    private val requestProcessor: RequestProcessor,
    @Value("\${meccano.port:8080}") private val port: Int
) {

    @Bean
    @ConditionalOnMissingBean(HttpServer::class)
    fun nioHttpServer(): HttpServer = NioServer(requestProcessor, port).also { it.startup() }

}