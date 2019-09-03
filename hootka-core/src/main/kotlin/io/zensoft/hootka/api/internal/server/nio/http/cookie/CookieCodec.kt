package io.zensoft.hootka.api.internal.server.nio.http.cookie

import io.netty.handler.codec.http.cookie.Cookie

interface CookieCodec {

    fun encode(cookies: List<Cookie>): String

    fun decode(string: String): Map<String, String>

}