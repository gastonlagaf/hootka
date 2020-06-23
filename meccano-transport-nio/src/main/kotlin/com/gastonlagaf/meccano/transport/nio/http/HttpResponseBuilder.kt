package com.gastonlagaf.meccano.transport.nio.http

import com.gastonlagaf.meccano.api.WrappedHttpResponse
import java.nio.ByteBuffer

interface HttpResponseBuilder {

    fun build(response: WrappedHttpResponse): ByteBuffer

}