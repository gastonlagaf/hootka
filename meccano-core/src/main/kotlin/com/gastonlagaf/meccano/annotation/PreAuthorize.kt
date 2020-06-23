package com.gastonlagaf.meccano.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class PreAuthorize(
    val value: String
)