package com.gastonlagaf.meccano.api.internal.mapper

import com.gastonlagaf.meccano.annotation.PathVariable
import com.gastonlagaf.meccano.api.HttpRequestMapper
import com.gastonlagaf.meccano.api.internal.path.PathMatcher
import com.gastonlagaf.meccano.api.internal.support.HandlerMethodParameter
import com.gastonlagaf.meccano.api.internal.support.HttpHandlerMetaInfo
import com.gastonlagaf.meccano.api.internal.support.RequestContext
import com.gastonlagaf.meccano.api.internal.utils.NumberUtils
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

class PathVariableMapper(
    private val pathMatcher: PathMatcher
) : HttpRequestMapper {

    override fun getSupportedAnnotation(): KClass<out Annotation> = PathVariable::class

    override fun supportsAnnotation(annotations: List<Annotation>): Boolean {
        return annotations.find { it is PathVariable } != null
    }

    override fun createValue(parameter: HandlerMethodParameter, context: RequestContext, handlerMethod: HttpHandlerMetaInfo): Any {
        val pathVariables = pathMatcher.getPathVariables(handlerMethod.path, context.request.getPath())
        return NumberUtils.parseNumber(pathVariables.getValue(parameter.name), parameter.clazz)
    }

    override fun mapParameter(parameter: KParameter, annotations: List<Annotation>): HandlerMethodParameter {
        val annotation = annotations.find { it is PathVariable } as PathVariable
        val patternName = if (annotation.value.isEmpty()) parameter.name else annotation.value
        return HandlerMethodParameter(patternName!!, parameter.type.javaType as Class<*>,
            parameter.type.isMarkedNullable, annotation)
    }

}