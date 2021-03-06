package io.zensoft.hootka.api

interface SessionStorage {

    fun findSession(id: String): HttpSession?

    fun createSession(): HttpSession

    fun createAndAssignSession(response: WrappedHttpResponse): HttpSession

    fun resolveSession(request: WrappedHttpRequest): HttpSession?

    fun removeSession(request: WrappedHttpRequest)

}