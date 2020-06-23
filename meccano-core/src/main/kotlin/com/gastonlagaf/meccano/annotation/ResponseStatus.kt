package com.gastonlagaf.meccano.annotation

import com.gastonlagaf.meccano.api.model.HttpResponseStatus

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class ResponseStatus(
    val value: HttpResponseStatus
)