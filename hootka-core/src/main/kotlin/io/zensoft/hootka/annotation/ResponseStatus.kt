package io.zensoft.hootka.annotation

import io.zensoft.hootka.api.model.HttpResponseStatus

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class ResponseStatus(
    val value: HttpResponseStatus
)