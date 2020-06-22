package io.zensoft.hootka.transport.nio.context

import io.zensoft.hootka.api.HttpHeaders
import io.zensoft.hootka.api.WrappedHttpResponse
import io.zensoft.hootka.api.model.HttpResponseStatus
import io.zensoft.hootka.api.model.MimeType
import io.zensoft.hootka.api.support.DefaultHttpHeaders
import io.zensoft.hootka.api.support.HttpCookie
import java.io.ByteArrayInputStream
import java.io.InputStream

class DefaultWrappedHttpResponse(
    private var httpStatus: HttpResponseStatus = HttpResponseStatus.OK,
    private var content: ByteArray = WrappedHttpResponse.EMPTY_ARRAY,
    private val headers: HttpHeaders = DefaultHttpHeaders(),
    private var contentType: MimeType = MimeType.APPLICATION_JSON,
    private val cookies: MutableList<HttpCookie> = mutableListOf()
) : WrappedHttpResponse {

    override fun getHttpStatus(): HttpResponseStatus = httpStatus

    override fun getContentStream(): InputStream = ByteArrayInputStream(content)

    override fun getContent(): ByteArray = content

    override fun getHeader(name: String): String? = headers.getOne(name)

    override fun getHeaders(): Map<String, List<String>> = headers.getAll()

    override fun getContentType(): MimeType = contentType

    override fun setHeader(key: String, value: String) {
        headers.add(key, value)
    }

    override fun setCookie(key: String, value: String, httpOnly: Boolean, maxAge: Long, path: String, secured: Boolean) {
        val cookie = HttpCookie(key, value, path, maxAge, httpOnly, secured)
        cookies.add(cookie)
    }

    override fun getCookies(): List<HttpCookie> = cookies

    override fun mutate(status: HttpResponseStatus, contentType: MimeType, content: ByteArray?) {
        this.httpStatus = status
        this.contentType = contentType
        content?.let { this.content = content }
    }
}