package io.zensoft.hootka.api.internal.provider

import io.zensoft.hootka.api.HttpResponseResolver
import io.zensoft.hootka.api.WrappedHttpResponse
import io.zensoft.hootka.api.model.MimeType
import org.springframework.context.ApplicationContext
import javax.annotation.PostConstruct

class ResponseResolverProvider(
    private val applicationContext: ApplicationContext
) {

    private lateinit var responseResolvers: Map<String, HttpResponseResolver>

    fun createResponseBody(result: Any, handlerArgs: Array<Any?>, mimeType: MimeType, response: WrappedHttpResponse): ByteArray {
        if (result === Unit) return ByteArray(0)
        return responseResolvers[mimeType.toString()]?.resolveResponseBody(result, handlerArgs, response) ?:
            throw IllegalArgumentException("Unsupported response content type $mimeType")
    }

    @PostConstruct
    private fun init() {
        responseResolvers = applicationContext.getBeansOfType(HttpResponseResolver::class.java)
            .values.associate { it.getContentType().toString() to it }
    }

}