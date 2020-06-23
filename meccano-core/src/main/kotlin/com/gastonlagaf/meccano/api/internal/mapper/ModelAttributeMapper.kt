package com.gastonlagaf.meccano.api.internal.mapper

import com.gastonlagaf.meccano.annotation.ModelAttribute
import com.gastonlagaf.meccano.api.HttpRequestMapper
import com.gastonlagaf.meccano.api.internal.HttpRequestParser
import com.gastonlagaf.meccano.api.internal.support.HandlerMethodParameter
import com.gastonlagaf.meccano.api.internal.support.HttpHandlerMetaInfo
import com.gastonlagaf.meccano.api.internal.support.RequestContext
import com.gastonlagaf.meccano.api.internal.utils.DeserializationUtils
import com.gastonlagaf.meccano.api.support.HttpHeaderTitles
import com.gastonlagaf.meccano.api.support.HttpMethod
import javax.validation.Valid
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

class ModelAttributeMapper : HttpRequestMapper {

    override fun getSupportedAnnotation(): KClass<out Annotation> = ModelAttribute::class

    override fun supportsAnnotation(annotations: List<Annotation>): Boolean {
        return annotations.find { it is ModelAttribute } != null
    }

    override fun createValue(parameter: HandlerMethodParameter, context: RequestContext, handlerMethod: HttpHandlerMetaInfo): Any {
        if (context.request.getMethod() == HttpMethod.POST) {
            val contentType = context.request.getHeader(HttpHeaderTitles.contentType.uppercasedValue)
            if ("application/x-www-form-urlencoded" == contentType) {
                val queryParams = HttpRequestParser(context.request.getContentAsString()).params()
                return DeserializationUtils.createBeanFromQueryString(parameter.clazz, queryParams)
            }
        }
        throw IllegalArgumentException("Model attribute processes only with post request with form encoded parameters")
    }

    override fun mapParameter(parameter: KParameter, annotations: List<Annotation>): HandlerMethodParameter {
        val annotation = annotations.find { it is ModelAttribute }
        val validationRequired = annotations.find { it is Valid } != null
        return HandlerMethodParameter(parameter.name!!, parameter.type.javaType as Class<*>,
            parameter.type.isMarkedNullable, annotation, validationRequired)
    }

}