package io.zensoft.hootka.api.internal.http

import io.zensoft.hootka.api.WrappedHttpRequest
import io.zensoft.hootka.api.internal.server.nio.http.HttpRequestParser
import io.zensoft.hootka.api.internal.server.nio.http.domain.RawHttpRequest
import io.zensoft.hootka.api.model.HttpMethod
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.charset.Charset

class DefaultWrappedHttpRequest(
        private val path: String,
        private val method: HttpMethod,
        private var params: String? = null,
        private val content: ByteArray,
        private val headers: Map<String, String>,
        private val address: SocketAddress?
) : WrappedHttpRequest {
    constructor(rawRequest: RawHttpRequest) : this(
            rawRequest.path!!,
            rawRequest.method!!,
            rawRequest.params,
            rawRequest.content!!,
            rawRequest.headers!!,
            rawRequest.socketAddress
    )

    override fun getPath(): String = path

    override fun getMethod(): HttpMethod = method

    override fun getQueryParameters(): Map<String, List<String>> = HttpRequestParser(params).params()

    override fun getContentStream(): InputStream = content.inputStream()

    override fun getContentAsString(charset: Charset): String = String(content, charset)

    override fun getHeader(key: String): String? = headers[key]

    override fun getCookies(): Map<String, String> = HttpRequestParser(headers["COOKIE"]!!).cookie()

    override fun getWrappedRequest(): Any = throw Exception()

    override fun getReferer(): String? = headers["REFERER"]

    override fun getRemoteAddress(): String = (address as InetSocketAddress).address.hostAddress

}
