package com.gastonlagaf.meccano.api.internal.response

import com.fasterxml.jackson.databind.ObjectMapper
import com.gastonlagaf.meccano.api.HttpResponseResolver
import com.gastonlagaf.meccano.api.WrappedHttpResponse
import com.gastonlagaf.meccano.api.model.MimeType

class JsonResponseResolver(
    private val jsonMapper: ObjectMapper
) : HttpResponseResolver {

    override fun getContentType(): MimeType = MimeType.APPLICATION_JSON

    override fun resolveResponseBody(result: Any, handlerArgs: Array<Any?>, response: WrappedHttpResponse): ByteArray {
        return jsonMapper.writeValueAsBytes(result)
    }

}