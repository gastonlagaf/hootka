package com.gastonlagaf.meccano.api.internal.mapper

import com.gastonlagaf.meccano.api.HttpRequestMapper
import com.gastonlagaf.meccano.api.internal.support.HandlerMethodParameter
import com.gastonlagaf.meccano.api.internal.support.HttpHandlerMetaInfo
import com.gastonlagaf.meccano.api.internal.support.RequestContext
import com.gastonlagaf.meccano.api.internal.utils.DeserializationUtils
import javax.validation.Valid
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

class ValidMapper : HttpRequestMapper {

    override fun getSupportedAnnotation(): KClass<out Annotation> = Valid::class

    override fun supportsAnnotation(annotations: List<Annotation>): Boolean {
        return annotations.size == 1 && annotations.find { it is Valid } != null
    }

    override fun createValue(parameter: HandlerMethodParameter, context: RequestContext, handlerMethod: HttpHandlerMetaInfo): Any? {
        return DeserializationUtils.createBeanFromQueryString(parameter.clazz, context.request.getQueryParameters())
    }

    override fun mapParameter(parameter: KParameter, annotations: List<Annotation>): HandlerMethodParameter {
        val annotation = annotations.find { it is Valid }
        return HandlerMethodParameter(parameter.name!!, parameter.type.javaType as Class<*>,
            parameter.type.isMarkedNullable, annotation, true)
    }


}