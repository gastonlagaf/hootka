package io.zensoft.hootka.api.internal.server.nio.http

import io.zensoft.hootka.api.WrappedHttpRequest
import io.zensoft.hootka.api.WrappedHttpResponse
import java.nio.ByteBuffer

interface HttpResponseBuilder {

    fun build(request: WrappedHttpRequest, response: WrappedHttpResponse): ByteBuffer

}