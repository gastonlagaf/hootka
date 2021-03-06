package io.zensoft.hootka.api.internal.response

import com.fasterxml.jackson.databind.ObjectMapper
import io.zensoft.hootka.api.HttpResponseResolver
import io.zensoft.hootka.api.WrappedHttpResponse
import io.zensoft.hootka.api.model.MimeType

class JsonResponseResolver(
    private val jsonMapper: ObjectMapper
) : HttpResponseResolver {

    override fun getContentType(): MimeType = MimeType.APPLICATION_JSON

    override fun resolveResponseBody(result: Any, handlerArgs: Array<Any?>, response: WrappedHttpResponse): ByteArray {
        return jsonMapper.writeValueAsBytes(result)
    }

}