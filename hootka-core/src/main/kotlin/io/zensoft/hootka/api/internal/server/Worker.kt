package io.zensoft.hootka.api.internal.server

import java.io.IOException
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.ArrayBlockingQueue

class Worker: Runnable {

    private val selector = Selector.open()
    private val acceptQueue = ArrayBlockingQueue<SocketChannel>(10000)

    fun accept(key: SelectionKey) {
        val socketChannel = key.channel() as ServerSocketChannel
        val channel = socketChannel.accept()
        acceptQueue.add(channel)
    }

    fun processIncoming() {
        val channel = acceptQueue.poll() ?: return
        channel.configureBlocking(false)
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true)
        channel.register(selector, SelectionKey.OP_READ)
    }

    override fun run() {
        while (true) {
            processIncoming()
            selector.select(50)
            val keys = selector.selectedKeys().iterator()
            while (keys.hasNext()) {
                val key = keys.next()
                keys.remove()
                if (key.isReadable) {
                    read(key)
                }
            }
        }
    }

    private fun read(key: SelectionKey) {
        val channel = key.channel() as SocketChannel
        try {
            val buffer = ByteBuffer.allocate(64)
            val numRead = channel.read(buffer)
            if (numRead == -1) {
                channel.close()
                key.cancel()
                return
            }
            val data = ByteArray(numRead)
            System.arraycopy(buffer.array(), 0, data, 0, numRead)
            val response = processRequestContent(String(data))
            channel.write(ByteBuffer.wrap(response))
        } catch (ex: IOException) {
            //
        }
    }

    private fun processRequestContent(content: String): ByteArray {
        val methodSeparator = content.indexOf(' ') + 1
        val pathSeparator = content.indexOf(' ', methodSeparator)
        val path = content.substring(methodSeparator, pathSeparator)
        val responseContent = if ("/status" == path) {
            "Hello World"
        } else {
            "Not found"
        }
        return wrapHttpResponse(responseContent)
    }

    private fun wrapHttpResponse(content: String): ByteArray {
        return arrayOf("HTTP/1.1 200 OK",
            "Content-Type: text/plain",
            "Content-Length: ${content.toByteArray().size}",
            "",
            content).joinToString("\r\n").toByteArray()

    }

}