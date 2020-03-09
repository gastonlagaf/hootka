package io.zensoft.hootka.api.internal.server.nio

import io.zensoft.hootka.api.internal.handler.BaseRequestProcessor
import io.zensoft.hootka.api.internal.server.nio.threads.DefaultWorkerPool
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import java.net.StandardSocketOptions
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.util.concurrent.Executors

@Component
class Server(
    requestProcessor: BaseRequestProcessor
) : ApplicationListener<ApplicationReadyEvent> {

    private val selector = Selector.open()
    private val workers = DefaultWorkerPool(Runtime.getRuntime().availableProcessors(), requestProcessor)

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        Executors.newSingleThreadExecutor().submit {
            startupServer()
        }
    }

    private fun startupServer() {
        workers.initialize()
        val serverSocketChannel = ServerSocketChannel.open()
        serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true)
        serverSocketChannel.configureBlocking(false)
        serverSocketChannel.socket().bind(InetSocketAddress(8082))
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)
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

}