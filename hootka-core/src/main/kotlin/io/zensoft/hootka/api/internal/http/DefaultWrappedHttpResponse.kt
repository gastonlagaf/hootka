package io.zensoft.hootka.api.internal.http

import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.cookie.Cookie
import io.netty.handler.codec.http.cookie.DefaultCookie
import io.zensoft.hootka.api.WrappedHttpResponse
import io.zensoft.hootka.api.model.MimeType
import io.zensoft.hootka.api.model.HttpResponseStatus
import java.io.ByteArrayInputStream
import java.io.InputStream

class DefaultWrappedHttpResponse(
    private var httpStatus: HttpResponseStatus = HttpResponseStatus.OK,
    private var content: ByteArray = WrappedHttpResponse.EMPTY_ARRAY,
    private val headers: HttpHeaders = DefaultHttpHeaders(),
    private var contentType: MimeType = MimeType.APPLICATION_JSON,
    private val cookies: MutableList<Cookie> = mutableListOf()
) : WrappedHttpResponse {

    override fun getHttpStatus(): HttpResponseStatus = httpStatus

    override fun getContentStream(): InputStream = ByteArrayInputStream(content)

    override fun getContent(): ByteArray = content

    override fun getHeader(name: String): String? = headers.get(name)

    override fun getHeaders(): Map<String, List<String>> = headers.names().associateWith { headers.getAll(it) }

    override fun getContentType(): MimeType = contentType

    override fun setHeader(key: String, value: String) {
        headers.add(key, value)
    }

    override fun setCookie(key: String, value: String, httpOnly: Boolean, maxAge: Long?) {
        val cookie = DefaultCookie(key, value)
        cookie.isHttpOnly = httpOnly
        cookie.setPath("/")
        maxAge?.let { cookie.setMaxAge(maxAge) }
        cookies.add(cookie)
    }

    override fun getCookies(): List<Cookie> = cookies

    override fun mutate(status: HttpResponseStatus, contentType: MimeType, content: ByteArray?) {
        this.httpStatus = status
        this.contentType = contentType
        content?.let { this.content = content }
    }
}