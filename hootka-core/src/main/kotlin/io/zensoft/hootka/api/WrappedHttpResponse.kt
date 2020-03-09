package io.zensoft.hootka.api

import io.netty.handler.codec.http.cookie.Cookie
import io.zensoft.hootka.api.model.MimeType
import io.zensoft.hootka.api.model.HttpResponseStatus
import java.io.InputStream

interface WrappedHttpResponse {

    fun getHttpStatus(): HttpResponseStatus

    fun getContentStream(): InputStream

    fun getContent(): ByteArray

    fun getHeader(name: String): String?

    fun getHeaders(): Map<String, List<String>>

    fun getContentType(): MimeType

    fun setHeader(key: String, value: String)

    fun setCookie(key: String, value: String, httpOnly: Boolean, maxAge: Long?)

    fun getCookies(): List<Cookie>

    fun mutate(status: HttpResponseStatus, contentType: MimeType, content: ByteArray? = null)

    companion object {
        val EMPTY_ARRAY: ByteArray = ByteArray(0)
    }

}