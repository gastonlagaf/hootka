package io.zensoft.hootka.api.internal.mapper

import io.zensoft.hootka.annotation.RequestParam
import io.zensoft.hootka.api.HttpRequestMapper
import io.zensoft.hootka.api.internal.HttpRequestParser
import io.zensoft.hootka.api.internal.support.*
import io.zensoft.hootka.api.internal.utils.NumberUtils
import io.zensoft.hootka.api.model.HttpMethod
import io.zensoft.hootka.api.support.HttpHeaderTitles
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

class RequestParameterMapper : HttpRequestMapper {

    override fun getSupportedAnnotation(): KClass<out Annotation> = RequestParam::class

    override fun supportsAnnotation(annotations: List<Annotation>): Boolean {
        return annotations.find { it is RequestParam } != null
    }

    override fun mapParameter(parameter: KParameter, annotations: List<Annotation>): HandlerMethodParameter {
        val annotation = annotations.find { it is RequestParam } as RequestParam
        val name = if (annotation.value.isEmpty()) parameter.name else annotation.value
        return HandlerMethodParameter(name!!, parameter.type.javaType as Class<*>,
            parameter.type.isMarkedNullable, annotation)
    }

    override fun createValue(parameter: HandlerMethodParameter, context: RequestContext, handlerMethod: HttpHandlerMetaInfo): Any? {
        val queryParams = if (HttpMethod.POST == context.request.getMethod()) {
            val contentType = context.request.getHeader(HttpHeaderTitles.contentType.uppercasedValue)
            if ("application/x-www-form-urlencoded" != contentType) {
                throw IllegalArgumentException("Cannot map request parameter. Mismatched content type for post request")
            }
            HttpRequestParser(context.request.getContentAsString()).params()
        } else {
            context.request.getQueryParameters()
        }
        val queryValues = queryParams[parameter.name] ?: if (parameter.nullable) {
            return null
        } else {
            throw IllegalArgumentException("Missing required query argument named ${parameter.name}")
        }
        if (queryValues.size > 1) {
            if (parameter.clazz.isArray) {
                return queryValues.toTypedArray()
            } else {
                throw IllegalArgumentException("Expected single argument, but got multiple one")
            }
        }
        return NumberUtils.parseNumber(queryValues.first(), parameter.clazz)
    }

}