package io.zensoft.hootka.api.model

enum class HttpResponseStatus(
    val code: Int,
    val statusName: String
) {
    OK(200, "OK"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CONFLICT(410, "Conflict"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");
}