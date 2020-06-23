package com.gastonlagaf.meccano.api

interface SessionHandler {

    fun getOrCreateSession(request: WrappedHttpRequest, response: WrappedHttpResponse): HttpSession

}