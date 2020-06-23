package com.gastonlagaf.meccano.transport.nio.http.response

import com.gastonlagaf.meccano.api.WrappedHttpResponse
import com.gastonlagaf.meccano.api.support.HttpHeaderTitles
import com.gastonlagaf.meccano.transport.nio.context.DefaultWrappedHttpResponse
import com.gastonlagaf.meccano.transport.nio.http.HttpResponseBuilder
import java.nio.ByteBuffer

class DefaultHttpResponseBuilder : HttpResponseBuilder {

    override fun build(response: WrappedHttpResponse): ByteBuffer {
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
        result.add("${HttpHeaderTitles.HTTP_1_1} ${response.getHttpStatus().code} ${response.getHttpStatus().name}")
        result.add("${HttpHeaderTitles.server.value}: Hootka")
//        result.add("$DATE: ${LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)}") // Too slow
        result.add("${HttpHeaderTitles.contentType.value}: ${response.getContentType().value}")
        result.add("${HttpHeaderTitles.contentLength.value}: ${response.getContent().size}")
        for (header in response.getHeaders()) {
            result.add("${header.key}: ${header.value.last()}")
        }
        result.add("\r\n")
        return result.joinToString("\r\n").toByteArray()
    }

}