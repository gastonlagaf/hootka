package com.gastonlagaf.meccano.api

import com.gastonlagaf.meccano.api.model.MimeType

interface HttpResponseResolver {

    fun getContentType(): MimeType

    fun resolveResponseBody(result: Any, handlerArgs: Array<Any?>, response: WrappedHttpResponse): ByteArray

}