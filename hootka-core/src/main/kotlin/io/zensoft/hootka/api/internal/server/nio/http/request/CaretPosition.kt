package io.zensoft.hootka.api.internal.server.nio.http.request

enum class CaretPosition {
    REQUEST_PATH,
    HEADERS,
    BODY,
    CONTENT_DISPOSITION,
    CONTENT_TYPE,
    END
}