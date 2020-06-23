package com.gastonlagaf.meccano.api.internal.support

import com.gastonlagaf.meccano.api.internal.invoke.MethodInvocation
import com.gastonlagaf.meccano.api.model.HttpResponseStatus
import com.gastonlagaf.meccano.api.model.MimeType
import com.gastonlagaf.meccano.api.support.HttpMethod

class HttpHandlerMetaInfo(
    private val instance: Any,
    private val handlerMethod: MethodInvocation,
    val parameters: List<HandlerMethodParameter>,
    val stateless: Boolean = false,
    val status: HttpResponseStatus = HttpResponseStatus.OK,
    val contentType: MimeType = MimeType.APPLICATION_JSON,
    val path: String = "",
    val httpMethod: HttpMethod = HttpMethod.GET,
    val preconditionExpression: String? = null
) {

    fun execute(args: Array<Any?>): Any? = handlerMethod.invoke(args)

}