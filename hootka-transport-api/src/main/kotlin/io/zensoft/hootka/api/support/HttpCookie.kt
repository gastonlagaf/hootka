package io.zensoft.hootka.api.support

data class HttpCookie(
    val name: String,
    val value: String,
    val path: String,
    val maxAge: Long,
    val httpOnly: Boolean,
    val secured: Boolean,
    val domain: String? = null
)