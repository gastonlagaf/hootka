package io.zensoft.hootka.api.internal.server.nio.http.response

import io.zensoft.hootka.api.WrappedHttpRequest
import io.zensoft.hootka.api.WrappedHttpResponse
import io.zensoft.hootka.api.internal.http.DefaultWrappedHttpResponse
import io.zensoft.hootka.api.internal.server.nio.http.HttpResponseBuilder
import io.zensoft.hootka.api.internal.support.HttpHeader.CONTENT_LENGTH
import io.zensoft.hootka.api.internal.support.HttpHeader.CONTENT_TYPE
import io.zensoft.hootka.api.internal.support.HttpHeader.DATE
import io.zensoft.hootka.api.internal.support.HttpHeader.HTTP_1_1
import io.zensoft.hootka.api.internal.support.HttpHeader.SERVER
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DefaultHttpResponseBuilder : HttpResponseBuilder {

    override fun build(request: WrappedHttpRequest, response: WrappedHttpResponse): ByteBuffer {
        response as DefaultWrappedHttpResponse
        val headers = headers(response)
        val buffer = ByteBuffer.allocate(headers.size.plus(response.getContent().size))
        buffer.put(headers)
        buffer.put(response.getContent())
        buffer.flip()
        return buffer
    }

    private fun headers(response: DefaultWrappedHttpResponse): ByteArray {
        val result = mutableListOf<String>()
        result.add("$HTTP_1_1 ${response.getHttpStatus().code} ${response.getHttpStatus().name}")
        result.add("$SERVER: Hootka")
        result.add("$DATE: ${LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)}")
        result.add("$CONTENT_TYPE: ${response.getContentType().value}")
        result.add("$CONTENT_LENGTH: ${response.getContent().size}")
        for (header in response.getHeaders()) {
            result.add("${header.key}: ${header.value.last()}")
        }
        result.add("\r\n")
        return result.joinToString("\r\n").toByteArray()
    }

}