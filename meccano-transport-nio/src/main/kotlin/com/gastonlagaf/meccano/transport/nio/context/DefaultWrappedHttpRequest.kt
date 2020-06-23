package com.gastonlagaf.meccano.transport.nio.context

import com.gastonlagaf.meccano.api.WrappedHttpRequest
import com.gastonlagaf.meccano.api.internal.HttpRequestParser
import com.gastonlagaf.meccano.api.internal.RawHttpRequest
import com.gastonlagaf.meccano.api.support.HttpHeaderTitles
import com.gastonlagaf.meccano.api.support.HttpMethod
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.charset.Charset

class DefaultWrappedHttpRequest(
    private val path: String,
    private val method: HttpMethod,
    private var params: String?,
    private val content: ByteArray,
    private val headers: Map<String, String>,
    private val address: SocketAddress?
) : WrappedHttpRequest {

    private var cookies: Map<String, String> = emptyMap()
    private var queryParams: Map<String, List<String>> = emptyMap()

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

    override fun getQueryParameters(): Map<String, List<String>> {
        if (queryParams.isEmpty()) {
            if (null != params) {
                queryParams = HttpRequestParser(params!!).params()
            }
        }
        return queryParams
    }

    override fun getContentStream(): InputStream = content.inputStream()

    override fun getContentAsString(charset: Charset): String = String(content, charset)

    override fun getHeader(key: String): String? = headers[key.toUpperCase()]

    override fun getCookies(): Map<String, String> {
        if (cookies.isEmpty()) {
            cookies = headers[HttpHeaderTitles.cookie.uppercasedValue]
                ?.let { HttpRequestParser(it).cookie() } ?: emptyMap()
        }
        return cookies
    }

    override fun getWrappedRequest(): Any = throw Exception()

    override fun getReferer(): String? = headers["REFERER"]

    override fun getRemoteAddress(): String = (address as InetSocketAddress).address.hostAddress

}
