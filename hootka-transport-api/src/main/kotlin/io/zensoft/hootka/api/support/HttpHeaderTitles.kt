package io.zensoft.hootka.api.support

object HttpHeaderTitles {

    const val HTTP_1_1 = "HTTP/1.1"

    val server = HeaderTitle("Server")

    val connection = HeaderTitle("Connection")

    val contentType = HeaderTitle("Content-Type")

    val contentLength = HeaderTitle("Content-Length")

    val location = HeaderTitle("Location")

    val date = HeaderTitle("Date")

    val cookie = HeaderTitle("Cookie")

    val cacheControl = HeaderTitle("Cache-Control")

}