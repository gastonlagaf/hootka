package io.zensoft.hootka.api.internal.server.nio.http.response

import io.zensoft.hootka.api.WrappedHttpRequest
import io.zensoft.hootka.api.WrappedHttpResponse
import io.zensoft.hootka.api.internal.server.nio.http.HttpResponseBuilder
import java.nio.ByteBuffer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DefaultHttpResponseBuilder: HttpResponseBuilder {

    override fun build(request: WrappedHttpRequest, response: WrappedHttpResponse): ByteBuffer {
        val responseAsBytes = response.getContent()?.let { headers(response) + it } ?: headers(response)
        return ByteBuffer.wrap(responseAsBytes)
    }

    private fun headers(response: WrappedHttpResponse): ByteArray {
        val result = mutableListOf<String>()
        result.add("Server: Hootka")
        result.add("Date: ${LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)}")
        result.add("HTTP/1.1 ${response.getHttpStatus().value.code()}")
        for (header in response.getHeaders()) {
            result.add("${header.key}: ${header.value.last()}")
        }
        result.add("\r\n")
        return result.joinToString("\r\n").toByteArray()
    }

}