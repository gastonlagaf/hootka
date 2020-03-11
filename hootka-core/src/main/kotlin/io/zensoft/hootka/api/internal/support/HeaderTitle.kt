package io.zensoft.hootka.api.internal.support

data class HeaderTitle(
    val value: String,
    val uppercasedValue: String = value.toUpperCase()
)