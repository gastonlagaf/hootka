package com.gastonlagaf.meccano.api.internal.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.gastonlagaf.meccano.annotation.RequestBody
import com.gastonlagaf.meccano.api.HttpRequestMapper
import com.gastonlagaf.meccano.api.internal.support.HandlerMethodParameter
import com.gastonlagaf.meccano.api.internal.support.HttpHandlerMetaInfo
import com.gastonlagaf.meccano.api.internal.support.RequestContext
import javax.validation.Valid
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

class RequestBodyMapper(
    private val jsonMapper: ObjectMapper
) : HttpRequestMapper {

    override fun getSupportedAnnotation(): KClass<out Annotation> = RequestBody::class

    override fun supportsAnnotation(annotations: List<Annotation>): Boolean {
        return annotations.find { it is RequestBody } != null
    }

    override fun mapParameter(parameter: KParameter, annotations: List<Annotation>): HandlerMethodParameter {
        val annotation = annotations.find { it is RequestBody }
        val validationRequired = annotations.find { it is Valid } != null
        return HandlerMethodParameter(parameter.name!!, parameter.type.javaType as Class<*>,
            parameter.type.isMarkedNullable, annotation, validationRequired)
    }

    override fun createValue(parameter: HandlerMethodParameter, context: RequestContext, handlerMethod: HttpHandlerMetaInfo): Any {
        val iStream = context.request.getContentStream()
        return jsonMapper.readValue(iStream, parameter.clazz)
    }

}