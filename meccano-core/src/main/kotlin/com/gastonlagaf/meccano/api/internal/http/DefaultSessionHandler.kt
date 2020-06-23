package com.gastonlagaf.meccano.api.internal.http

import com.gastonlagaf.meccano.api.*

class DefaultSessionHandler(
    private val sessionStorage: SessionStorage,
    private val cookieName: String
) : SessionHandler {

    override fun getOrCreateSession(request: WrappedHttpRequest, response: WrappedHttpResponse): HttpSession {
        val cookies = request.getCookies()
        val sessionId = cookies[cookieName]
        var session = sessionId
            ?.let { sessionStorage.findSession(sessionId) }
        if (session == null) {
            session = sessionStorage.createAndAssignSession(response)
        }
        return session
    }

}