package io.zensoft.hootka.api.internal.server.nio.http.cookie

import io.netty.handler.codec.http.cookie.Cookie
import io.zensoft.hootka.api.internal.server.nio.http.request.HttpRequestParser

class DefaultCookieCodec : CookieCodec {

    override fun encode(cookies: List<Cookie>): String {
        return cookies.asSequence().map { "Set-Cookie: ${it.name()}=${it.value()}; Path=${it.path()}; Domain; Expires; HttpOnly; Secure" }.joinToString("; \n")
    }

    override fun decode(string: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val reader = HttpRequestParser(string)
        while (!reader.contentRead()) {
            val key = reader.readUntil('=').trim()
            result[key] = reader.readUntil(';').trim()
        }
        return result
    }

}