package com.gastonlagaf.meccano.api.internal.provider

import com.gastonlagaf.meccano.api.HttpResponseResolver
import com.gastonlagaf.meccano.api.WrappedHttpResponse
import com.gastonlagaf.meccano.api.model.MimeType

class ResponseResolverProvider(
    private val componentsStorage: ComponentsStorage
) {

    private lateinit var responseResolvers: Map<String, HttpResponseResolver>

    fun createResponseBody(result: Any, handlerArgs: Array<Any?>, mimeType: MimeType, response: WrappedHttpResponse): ByteArray {
        if (result === Unit) return ByteArray(0)
        return responseResolvers[mimeType.toString()]?.resolveResponseBody(result, handlerArgs, response)
            ?: throw IllegalArgumentException("Unsupported response content type $mimeType")
    }

    fun init() {
        responseResolvers = componentsStorage.getResponseResolvers().associateBy { it.getContentType().toString() }
    }

}