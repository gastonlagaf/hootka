package io.zensoft.hootka.api.internal.server.nio.http.domain

import io.zensoft.hootka.api.WrappedHttpResponse
import io.zensoft.hootka.api.model.HttpStatus.*

class HttpResponse(
    private val wrappedResponse: WrappedHttpResponse
) {
    private val result = mutableListOf<String>()
    private val content = wrappedResponse.getContent()


    fun buildResponse() {

        when (wrappedResponse.getHttpStatus()) {
            OK -> {

            }
            FOUND -> {

            }
            CONFLICT -> {

            }
            FORBIDDEN -> {

            }
            UNAUTHORIZED -> {

            }
            NOT_FOUND -> {

            }
            INTERNAL_SERVER_ERROR -> {

            }
            BAD_REQUEST -> {

            }

            else -> throw Exception("la-la-la")
        }
    }

//    private fun create() {
//        result.run {
//            add(HTTP_VERSION.plus(wrappedResponse.getHttpStatus().value))
//            add(DATE.plus())
//            add(SERVER.plus())
//            add(LAST_MODIFIED.plus())
//            add(CONTENT_LENGTH.plus(content.size))
//            add(CONTENT_TYPE.plus())
//            add(CONNECTION.plus())
//            add(BODY_SEPARATOR)
//            if (null != content) {
//                add(String(content))
//            } else {
//                add(BODY_SEPARATOR)
//            }
//        }
//
//    }

    companion object {
        private const val DATE = "Date: "
        private const val SERVER = "Server: "
        private const val LAST_MODIFIED = "Last-Modified: "
        private const val CONTENT_LENGTH = "Content-Length: "
        private const val CONTENT_TYPE = "Content-Type: "
        private const val CONNECTION = "Connection: "
        private const val BODY_SEPARATOR = "\r\n"
        private const val LOCATION = "Location: "

        private const val FORWARDED_FOR = "X-Forwarded-For: "
        private const val FORWARDED_HOST = "X-Forwarded-Host: "
        private const val FORWARDED_PORT = "X-Forwarded-Port: "
        private const val FORWARDED_PROTO = "X-Forwarded-Proto: "

        private const val HTTP_VERSION = "HTTP/1.1 "
    }
}
