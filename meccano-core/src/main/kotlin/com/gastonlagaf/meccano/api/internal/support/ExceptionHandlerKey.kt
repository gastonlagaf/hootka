package com.gastonlagaf.meccano.api.internal.support

import kotlin.reflect.KClass

data class ExceptionHandlerKey(
    val exceptionType: KClass<out Throwable>,
    val responseContentType: String
)