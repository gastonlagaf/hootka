package io.zensoft.hootka.api.internal.server.nio.http

import io.zensoft.hootka.api.WrappedHttpRequest
import io.zensoft.hootka.api.internal.http.DefaultWrappedHttpRequest
import io.zensoft.hootka.api.internal.server.nio.http.domain.RawHttpRequest
import java.io.ByteArrayOutputStream
import java.net.SocketAddress
import java.nio.ByteBuffer

class DefaultHttpRequestChunkCollector(
        private val bufferCapacity: Int,
        private val headersStream: ByteArrayOutputStream = ByteArrayOutputStream(bufferCapacity),
        private var bodyStream: ByteArrayOutputStream = ByteArrayOutputStream(bufferCapacity),
        private val bodyDelimiterPosition: MutableList<Int> = mutableListOf(),
        private var headersCollected: Boolean = false,
        private var request: RawHttpRequest = RawHttpRequest()
) : HttpRequestChunkCollector {

    override fun collect(chunk: ByteBuffer) {
        chunk.flip()
        val available = chunk.remaining()
        if (headersStream.size() + bodyStream.size() + available > bufferCapacity) {
            throw IllegalArgumentException()
        }
        while (chunk.remaining() > 0) {
            val byte = chunk.get()
            if (!headersCollected) {
                headersStream.write(byte.toInt())
                controlHeadersRead(byte)
            } else {
                bodyStream.write(byte.toInt())
            }
        }
    }

    override fun aggregate(address: SocketAddress): WrappedHttpRequest {
        request.apply {
            content = bodyStream.toByteArray()
            socketAddress = address
        }
        val wrappedRequest = DefaultWrappedHttpRequest(request)
        reset()
        return wrappedRequest
    }

    override fun requestRead(): Boolean {
        return (headersCollected && request.headers?.get("CONTENT-LENGTH")?.toInt() == bodyStream.size()) || (headersCollected &&"GET" == request.method?.name)
    }

    private fun controlHeadersRead(byte: Byte) {
        if (byte == NEW_LINE_BYTE || byte == CARET_RESET_BYTE) {
            bodyDelimiterPosition.add(headersStream.size())
            if (bodyDelimiterPosition.size == DELIMITER_NUM_BYTES) {
                headersCollected = true
                request = parseHeaders(headersStream.toByteArray())
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

        while (reader.headersAvailable()) {
            val header = reader.header()
            readHeaders[header.first.toUpperCase()] = header.second.trim()
        }
        return request.apply { headers = readHeaders }
    }

    private fun reset() {
        headersStream.reset()
        bodyStream.reset()
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
