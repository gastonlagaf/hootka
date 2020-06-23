package com.gastonlagaf.meccano.api.model

import com.gastonlagaf.meccano.api.WrappedHttpRequest
import com.gastonlagaf.meccano.api.WrappedHttpResponse

class SimpleAuthenticationDetails(
    val username: String,
    val password: String,
    val request: WrappedHttpRequest,
    val response: WrappedHttpResponse,
    val rememberMe: Boolean = false
)