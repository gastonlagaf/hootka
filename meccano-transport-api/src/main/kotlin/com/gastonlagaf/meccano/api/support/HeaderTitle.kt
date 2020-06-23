package com.gastonlagaf.meccano.api.support

data class HeaderTitle(
    val value: String,
    val uppercasedValue: String = value.toUpperCase()
)