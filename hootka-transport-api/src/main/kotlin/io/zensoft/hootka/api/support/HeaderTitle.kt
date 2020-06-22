package io.zensoft.hootka.api.support

data class HeaderTitle(
    val value: String,
    val uppercasedValue: String = value.toUpperCase()
)