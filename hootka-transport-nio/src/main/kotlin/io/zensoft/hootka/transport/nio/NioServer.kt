package io.zensoft.hootka.transport.nio

import io.zensoft.hootka.api.HttpServer
import io.zensoft.hootka.api.RequestProcessor
import io.zensoft.hootka.transport.nio.threads.DefaultWorkerPool
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.StandardSocketOptions
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.util.concurrent.Executors

class NioServer(
    requestProcessor: RequestProcessor,
    private val port: Int
): HttpServer {

    private val selector = Selector.open()
    private val workers = DefaultWorkerPool(Runtime.getRuntime().availableProcessors(), requestProcessor)

    override fun startup() {
        Executors.newSingleThreadExecutor().submit {
            startupServer()
        }
    }

    private fun startupServer() {
        workers.initialize()

        val serverSocketChannel = ServerSocketChannel.open()
        serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true)
        serverSocketChannel.configureBlocking(false)
        serverSocketChannel.socket().bind(InetSocketAddress(port))
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)
        log.info("Server listening on port $port")

        while (true) {
            selector.select()
            val keys = selector.selectedKeys().iterator()
            while (keys.hasNext()) {
                val key = keys.next()
                keys.remove()
                if (!key.isValid) {
                    continue
                }
                if (key.isAcceptable) {
                    workers.next().accept(key.channel())
                }
            }
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(NioServer::class.java)
    }

}