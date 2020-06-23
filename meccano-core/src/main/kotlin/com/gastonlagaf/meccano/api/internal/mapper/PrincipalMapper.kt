package com.gastonlagaf.meccano.api.internal.mapper

import com.gastonlagaf.meccano.annotation.Principal
import com.gastonlagaf.meccano.api.HttpRequestMapper
import com.gastonlagaf.meccano.api.SecurityProvider
import com.gastonlagaf.meccano.api.internal.support.HandlerMethodParameter
import com.gastonlagaf.meccano.api.internal.support.HttpHandlerMetaInfo
import com.gastonlagaf.meccano.api.internal.support.RequestContext
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

class PrincipalMapper(
    private val securityProvider: SecurityProvider<*>
) : HttpRequestMapper {

    override fun getSupportedAnnotation(): KClass<out Annotation> = Principal::class

    override fun supportsAnnotation(annotations: List<Annotation>): Boolean {
        return null != annotations.find { it is Principal }
    }

    override fun mapParameter(parameter: KParameter, annotations: List<Annotation>): HandlerMethodParameter {
        val annotation = annotations.find { it is Principal }
        return HandlerMethodParameter(parameter.name!!, parameter.type.javaType as Class<*>,
            parameter.type.isMarkedNullable, annotation)
    }

    override fun createValue(parameter: HandlerMethodParameter, context: RequestContext, handlerMethod: HttpHandlerMetaInfo): Any? {
        return securityProvider.findPrincipal(context)
    }

}