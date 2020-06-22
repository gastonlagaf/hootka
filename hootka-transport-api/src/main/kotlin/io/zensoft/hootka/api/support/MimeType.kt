package io.zensoft.hootka.api.model

enum class MimeType(val value: String) {
    TEXT_PLAIN("text/plain"),
    APPLICATION_JSON("application/json"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    BYTES("bytes"),
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_JAVASCRIPT("text/javascript"),
    IMAGE_GIF("image/gif"),
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_SVG("image/svg+xml"),
    IMAGE_ICO("image/x-icon"),
    FONT_TTF("font/ttf"),
    FONT_WOFF2("font/woff2")
}