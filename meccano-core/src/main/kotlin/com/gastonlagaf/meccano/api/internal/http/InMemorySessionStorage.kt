package com.gastonlagaf.meccano.api.internal.http

import com.gastonlagaf.meccano.api.HttpSession
import com.gastonlagaf.meccano.api.SessionStorage
import com.gastonlagaf.meccano.api.WrappedHttpRequest
import com.gastonlagaf.meccano.api.WrappedHttpResponse
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

class InMemorySessionStorage(
    private val cookieName: String,
    private val cookieExpiry: Long
) : SessionStorage {

    private lateinit var storage: Cache<String, HttpSession>

    override fun findSession(id: String): HttpSession? {
        return storage.getIfPresent(id)
    }

    override fun createSession(): HttpSession {
        val sessionId = UUID.randomUUID().toString()
        val session = DefaultHttpSession(sessionId)
        storage.put(sessionId, session)
        return session
    }

    override fun createAndAssignSession(response: WrappedHttpResponse): HttpSession {
        val session = this.createSession()
        response.setCookie(cookieName, session.getId(), true)
        return session
    }

    override fun resolveSession(request: WrappedHttpRequest): HttpSession? {
        val cookie = request.getCookies()[cookieName]
        return cookie?.let { findSession(it) }
    }

    override fun removeSession(request: WrappedHttpRequest) {
        val cookie = request.getCookies()[cookieName]
        cookie?.let { storage.invalidate(it) }
    }

    @PostConstruct
    private fun init() {
        storage = Caffeine.newBuilder()
            .expireAfterAccess(cookieExpiry, TimeUnit.SECONDS)
            .build<String, HttpSession>()
    }

}