package io.zensoft.hootka.api.internal.server.nio.http.cookie

interface CookieCodec {

    fun encode(cookies: Map<String, String>): String

    fun decode(string: String): Map<String, String>

}