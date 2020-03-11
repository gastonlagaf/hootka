package io.zensoft.hootka.api.internal.server.nio.http.request

import io.zensoft.hootka.api.WrappedHttpRequest
import io.zensoft.hootka.api.internal.http.DefaultWrappedHttpRequest
import io.zensoft.hootka.api.internal.server.nio.http.HttpRequestChunkCollector
import io.zensoft.hootka.api.internal.server.nio.http.domain.QueueMessage
import io.zensoft.hootka.api.internal.server.nio.http.domain.RawHttpRequest
import java.net.SocketAddress
import java.nio.ByteBuffer

class DefaultHttpRequestChunkCollector(
    private val bufferCapacity: Int,
    private val header: QueueMessage = QueueMessage(bufferCapacity),
    private var body: QueueMessage = QueueMessage(bufferCapacity),
    private val bodyDelimiterPosition: MutableList<Int> = mutableListOf(),
    private var headersCollected: Boolean = false,
    private var request: RawHttpRequest = RawHttpRequest()
) : HttpRequestChunkCollector {

    override fun collect(chunk: ByteBuffer) {
        chunk.flip()
        val available = chunk.remaining()
        if (header.size + body.size + available > 12345676) {
            throw IllegalArgumentException()
        }
        while (chunk.remaining() > 0) {
            val byte = chunk.get()
            if (!headersCollected) {
                header.put(byte)
                controlHeadersRead(byte)
            } else {
                body.put(byte)
            }
        }
    }

    override fun aggregate(address: SocketAddress): WrappedHttpRequest {
        request.apply {
            content = body.readMessage()
            socketAddress = address
        }
        val wrappedRequest = DefaultWrappedHttpRequest(request)
        reset()
        return wrappedRequest
    }

    override fun requestRead(): Boolean {
        return (headersCollected && request.headers?.get("CONTENT-LENGTH")?.toInt() == body.size) || (headersCollected && "GET" == request.method?.name)
    }

    private fun controlHeadersRead(byte: Byte) {
        if (byte == NEW_LINE_BYTE || byte == CARET_RESET_BYTE) {
            bodyDelimiterPosition.add(header.size)
            if (bodyDelimiterPosition.size == DELIMITER_NUM_BYTES) {
                headersCollected = true
                request = parseHeaders(header.readMessage())
            }
        } else {
            bodyDelimiterPosition.clear()
        }
    }

    private fun parseHeaders(bytes: ByteArray): RawHttpRequest {
        val content = String(bytes)
        val readHeaders = mutableMapOf<String, String>()
        val reader = HttpRequestParser(content)
        reader.requestPath(request)

        do {
            val header = reader.header()
            readHeaders[header.first.toUpperCase()] = header.second.trim()
        } while (reader.headersAvailable())
        return request.apply { headers = readHeaders }
    }

    private fun reset() {
        bodyDelimiterPosition.clear()
        headersCollected = false
        request = RawHttpRequest()
    }

    companion object {
        private const val NEW_LINE_BYTE: Byte = 10
        private const val CARET_RESET_BYTE: Byte = 13
        private const val DELIMITER_NUM_BYTES: Int = 4
    }

}
