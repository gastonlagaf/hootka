package com.gastonlagaf.meccano.api

import com.gastonlagaf.meccano.api.internal.support.HandlerMethodParameter
import com.gastonlagaf.meccano.api.internal.support.HttpHandlerMetaInfo
import com.gastonlagaf.meccano.api.internal.support.RequestContext
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

interface HttpRequestMapper {

    fun getSupportedAnnotation(): KClass<out Annotation>

    fun supportsAnnotation(annotations: List<Annotation>): Boolean

    fun createValue(parameter: HandlerMethodParameter, context: RequestContext, handlerMethod: HttpHandlerMetaInfo): Any?

    fun mapParameter(parameter: KParameter, annotations: List<Annotation>): HandlerMethodParameter

}