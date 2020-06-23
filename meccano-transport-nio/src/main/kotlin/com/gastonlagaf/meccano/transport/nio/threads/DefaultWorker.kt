package com.gastonlagaf.meccano.transport.nio.threads

import com.gastonlagaf.meccano.api.RequestProcessor
import com.gastonlagaf.meccano.transport.nio.context.DefaultWrappedHttpResponse
import com.gastonlagaf.meccano.transport.nio.http.HttpRequestChunkCollector
import com.gastonlagaf.meccano.transport.nio.http.HttpResponseBuilder
import com.gastonlagaf.meccano.transport.nio.http.request.DefaultHttpRequestChunkCollector
import com.gastonlagaf.meccano.transport.nio.http.response.DefaultHttpResponseBuilder
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.*
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

class DefaultWorker(
    bufferSize: Int,
    private val requestProcessor: RequestProcessor
) : Thread(), Worker {

    @Volatile
    private var running: Boolean = true

    private val buffer: ByteBuffer = ByteBuffer.allocate(bufferSize)

    private val responseBuilder: HttpResponseBuilder = DefaultHttpResponseBuilder()

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
            readCycle()
        }
    }

    override fun shutdown() {
        running = false
    }

    private fun readCycle() {
        registerAccepted()
        selector.select(50)
        val keysIterator = selector.selectedKeys().iterator()
        while (keysIterator.hasNext()) {
            buffer.clear()
            val key = keysIterator.next()
            keysIterator.remove()
            if (key.isValid && key.isReadable) {
                read(key)
            }
        }
    }

    private fun registerAccepted() {
        val channel = pendingAcceptQueue.poll() ?: return
        channel.configureBlocking(false)
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true)
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true)
        channel.setOption(StandardSocketOptions.SO_SNDBUF, 256)
        val key = channel.register(selector, SelectionKey.OP_READ)
        key.attach(DefaultHttpRequestChunkCollector(256))
    }

    private fun read(key: SelectionKey) {
        val channel = key.channel() as SocketChannel
        try {
            val collector = key.attachment() as HttpRequestChunkCollector
            val readBytes = channel.read(buffer)
            if (readBytes == -1) {
                channel.close()
                key.cancel()
            } else if (!collector.requestRead()) {
                collector.collect(buffer)
                if (collector.requestRead()) {
                    val address = channel.remoteAddress
                    val wrappedRequest = collector.aggregate(address)
                    val wrappedResponse = DefaultWrappedHttpResponse()
                    requestProcessor.process(wrappedRequest, wrappedResponse)
                    val serializedResponse = responseBuilder.build(wrappedResponse)
                    channel.write(serializedResponse)
                }
            }
        } catch (ex: Exception) {
            channel.close()
            key.cancel()
        }
    }

}