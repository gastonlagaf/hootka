package com.gastonlagaf.meccano.api

import com.gastonlagaf.meccano.api.support.HttpMethod
import java.io.InputStream
import java.nio.charset.Charset

interface WrappedHttpRequest {

    fun getPath(): String

    fun getMethod(): HttpMethod

    fun getQueryParameters(): Map<String, List<String>>

    fun getContentStream(): InputStream

    fun getContentAsString(charset: Charset = Charset.defaultCharset()): String

    fun getHeader(key: String): String?

    fun getCookies(): Map<String, String>

    fun getWrappedRequest(): Any

    fun getReferer(): String?

    fun getRemoteAddress(): String

}