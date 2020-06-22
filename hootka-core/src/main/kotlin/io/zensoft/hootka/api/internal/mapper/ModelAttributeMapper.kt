package io.zensoft.hootka.api.internal.mapper

import io.zensoft.hootka.annotation.ModelAttribute
import io.zensoft.hootka.api.HttpRequestMapper
import io.zensoft.hootka.api.internal.HttpRequestParser
import io.zensoft.hootka.api.internal.support.HandlerMethodParameter
import io.zensoft.hootka.api.internal.support.HttpHandlerMetaInfo
import io.zensoft.hootka.api.internal.support.RequestContext
import io.zensoft.hootka.api.internal.utils.DeserializationUtils
import io.zensoft.hootka.api.model.HttpMethod
import io.zensoft.hootka.api.support.HttpHeaderTitles
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