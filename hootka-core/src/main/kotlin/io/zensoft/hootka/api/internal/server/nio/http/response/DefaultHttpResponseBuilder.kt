package io.zensoft.hootka.api.internal.server.nio.http.response

import io.zensoft.hootka.api.WrappedHttpResponse
import io.zensoft.hootka.api.internal.http.DefaultWrappedHttpResponse
import io.zensoft.hootka.api.internal.server.nio.http.HttpResponseBuilder
import io.zensoft.hootka.api.internal.support.HttpHeaderTitles
import io.zensoft.hootka.api.internal.support.HttpHeaderTitles.HTTP_1_1
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
        result.add("$HTTP_1_1 ${response.getHttpStatus().code} ${response.getHttpStatus().name}")
        result.add("${HttpHeaderTitles.server.value}: Hootka")
//        result.add("$DATE: ${LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)}")
        result.add("${HttpHeaderTitles.contentType.value}: ${response.getContentType().value}")
        result.add("${HttpHeaderTitles.contentLength.value}: ${response.getContent().size}")
        for (header in response.getHeaders()) {
            result.add("${header.key}: ${header.value.last()}")
        }
        result.add("\r\n")
        return result.joinToString("\r\n").toByteArray()
    }

}