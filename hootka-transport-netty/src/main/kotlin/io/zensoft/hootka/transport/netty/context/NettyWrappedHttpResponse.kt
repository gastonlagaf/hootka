package io.zensoft.hootka.transport.netty.context

import io.netty.handler.codec.http.DefaultHttpHeaders
import io.zensoft.hootka.api.WrappedHttpResponse
import io.zensoft.hootka.api.model.HttpResponseStatus
import io.zensoft.hootka.api.model.MimeType
import io.zensoft.hootka.api.support.HttpCookie
import java.io.ByteArrayInputStream
import java.io.InputStream

class NettyWrappedHttpResponse(
    private var httpStatus: HttpResponseStatus = HttpResponseStatus.OK,
    private var contentType: MimeType = MimeType.APPLICATION_JSON,
    private var content: ByteArray = WrappedHttpResponse.EMPTY_ARRAY,
    private val headers: DefaultHttpHeaders = DefaultHttpHeaders(),
    private val cookies: MutableList<HttpCookie> = mutableListOf()
) : WrappedHttpResponse {

    override fun getContent(): ByteArray {
        return content
    }

    override fun getHttpStatus(): HttpResponseStatus {
        return httpStatus
    }

    override fun getContentStream(): InputStream {
        return ByteArrayInputStream(content)
    }

    override fun getHeader(name: String): String? {
        return headers.get(name)
    }

    override fun getHeaders(): Map<String, List<String>> {
        return headers.associate { it.key to listOf(it.value) }
    }

    override fun getContentType(): MimeType {
        return contentType
    }

    override fun setHeader(key: String, value: String) {
        headers.add(key, value)
    }

    override fun setCookie(key: String, value: String, httpOnly: Boolean, maxAge: Long, path: String, secured: Boolean) {
        val cookie = HttpCookie(key, value, path, maxAge, httpOnly, secured)
        cookies.add(cookie)
    }

    override fun getCookies(): List<HttpCookie> {
        return cookies
    }

    override fun mutate(status: HttpResponseStatus, contentType: MimeType, content: ByteArray?) {
        this.httpStatus = status
        this.contentType = contentType
        content?.let { this.content = content }
    }

}
