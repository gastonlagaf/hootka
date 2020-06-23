package com.gastonlagaf.meccano.api.internal.support

import com.gastonlagaf.meccano.api.HttpSession
import com.gastonlagaf.meccano.api.WrappedHttpRequest
import com.gastonlagaf.meccano.api.WrappedHttpResponse

class RequestContext(
    val request: WrappedHttpRequest,
    val response: WrappedHttpResponse,
    var session: HttpSession? = null
)