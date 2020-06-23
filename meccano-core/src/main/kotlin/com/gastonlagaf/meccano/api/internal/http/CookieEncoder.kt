package com.gastonlagaf.meccano.api.internal.http

import com.gastonlagaf.meccano.api.support.HttpCookie

object CookieEncoder {

    fun encode(cookies: List<HttpCookie>): List<String> {
        return cookies.map {
            "Set-Cookie: ${it.name}=${it.value}; Path=${it.path}; Domain=${it.domain}; Expires=${it.maxAge}; ${if (it.secured) "HttpOnly;" else ""}"
        }
    }

}