package io.zensoft.hootka.api.internal.server.nio.threads


import io.zensoft.hootka.api.WrappedHttpResponse
import io.zensoft.hootka.api.internal.handler.BaseRequestProcessor
import io.zensoft.hootka.api.internal.http.DefaultWrappedHttpResponse
import io.zensoft.hootka.api.internal.server.nio.http.DefaultHttpRequestChunkCollector
import io.zensoft.hootka.api.internal.server.nio.http.HttpRequestChunkCollector
import io.zensoft.hootka.api.internal.server.nio.http.cookie.DefaultCookieCodec
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.*
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

class DefaultWorker(
        bufferSize: Int,
        private val requestProcessor: BaseRequestProcessor
) : Thread(), Worker {

    @Volatile
    private var running: Boolean = true

    private val buffer: ByteBuffer = ByteBuffer.allocate(bufferSize)

    private val selector: Selector = Selector.open()
    private val pendingAcceptQueue: Queue<SocketChannel> = ArrayBlockingQueue(1000) // TODO: configure


    override fun initialize() {
        start()
    }

    override fun accept(selectableChannel: SelectableChannel) {
        val socketChannel = selectableChannel as ServerSocketChannel
        val channel = socketChannel.accept()
        pendingAcceptQueue.add(channel)
    }

    override fun run() {
        while (running) {
            registerAccepted()
            selector.select(50)
            val keysIterator = selector.selectedKeys().iterator()
            while (keysIterator.hasNext()) {
                buffer.clear()
                val key = keysIterator.next()
                keysIterator.remove()
                if (key.isValid && key.isReadable) {
                    val collector = key.attachment() as HttpRequestChunkCollector
                    val channel = key.channel() as SocketChannel
                    val readBytes = channel.read(buffer)
                    if (readBytes == -1) {
                        channel.close()
                        key.cancel()
                        continue
                    } else if (!collector.requestRead()) {
                        collector.collect(buffer)
                        if (collector.requestRead()) {
                            val address = channel.remoteAddress
                            val wrappedRequest = collector.aggregate(address)
                            val wrappedResponse = DefaultWrappedHttpResponse()
                            requestProcessor.processRequest(wrappedRequest, wrappedResponse)
                            channel.write(ByteBuffer.wrap(responseBuilder(wrappedResponse)))
                        }
                    }
                }
            }
        }
    }

    override fun shutdown() {
        running = false
    }

    private fun responseBuilder(wrappedResponse: WrappedHttpResponse): ByteArray? {
        val content = wrappedResponse.getContent()
        val result = mutableListOf<String>()
        result.add("HTTP/1.1 ${wrappedResponse.getHttpStatus().value}")
        result.add("Date: ")
        result.add("Server: ")
        result.add("Last-Modified: ")
        result.add("Content-Length: ${content!!.size}")
        result.add("Content-Type: ${wrappedResponse.getContentType().value}")
        val cookie = wrappedResponse.getCookies()
        if (cookie.isNotEmpty()) {
            result.add(DefaultCookieCodec().encode(cookie))
        }
        result.add("Connection: ")
        result.add("")
        result.add(String(content))
        return result.joinToString("\r\n").toByteArray()
    }

    private fun registerAccepted() {
        val channel = pendingAcceptQueue.poll() ?: return
        channel.configureBlocking(false)
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true)
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true)
        channel.setOption(StandardSocketOptions.SO_SNDBUF, 256)
        val key = channel.register(selector, SelectionKey.OP_READ)
        key.attach(DefaultHttpRequestChunkCollector(163840))
    }

}