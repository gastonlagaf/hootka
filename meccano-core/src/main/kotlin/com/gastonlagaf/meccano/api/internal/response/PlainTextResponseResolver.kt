package com.gastonlagaf.meccano.api.internal.response

import com.gastonlagaf.meccano.api.HttpResponseResolver
import com.gastonlagaf.meccano.api.WrappedHttpResponse
import com.gastonlagaf.meccano.api.model.MimeType

class PlainTextResponseResolver : HttpResponseResolver {

    override fun getContentType(): MimeType = MimeType.TEXT_PLAIN

    override fun resolveResponseBody(result: Any, handlerArgs: Array<Any?>, response: WrappedHttpResponse): ByteArray {
        if (result !is String) throw IllegalArgumentException("String return type should be for text/plain methods")
        return result.toString().toByteArray()
    }

}