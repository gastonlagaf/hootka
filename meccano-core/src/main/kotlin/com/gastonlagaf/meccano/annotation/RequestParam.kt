package com.gastonlagaf.meccano.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class RequestParam(
    val value: String = ""
)