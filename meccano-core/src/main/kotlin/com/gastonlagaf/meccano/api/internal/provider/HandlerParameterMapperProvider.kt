package com.gastonlagaf.meccano.api.internal.provider

import com.gastonlagaf.meccano.api.HttpRequestMapper
import com.gastonlagaf.meccano.api.ValidationProvider
import com.gastonlagaf.meccano.api.exceptions.HandlerParameterInstantiationException
import com.gastonlagaf.meccano.api.internal.support.HandlerMethodParameter
import com.gastonlagaf.meccano.api.internal.support.HttpHandlerMetaInfo
import com.gastonlagaf.meccano.api.internal.support.RequestContext
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaType

class HandlerParameterMapperProvider(
    private val componentsStorage: ComponentsStorage,
    private val validationService: ValidationProvider
) {

    private lateinit var mappers: Map<String, HttpRequestMapper>

    fun createParameterValue(parameter: HandlerMethodParameter, context: RequestContext, handlerMethod: HttpHandlerMetaInfo): Any? {
        val mapper = mappers[parameter.annotation!!.annotationClass.simpleName]
            ?: throw IllegalArgumentException("Unknown annotation type to map handler parameter. Annotation: ${parameter.annotation}")

        val argument = try {
            mapper.createValue(parameter, context, handlerMethod)
        } catch (ex: Exception) {
            throw HandlerParameterInstantiationException(ex.message)
        }
        if (argument != null && parameter.validationRequired) {
            validationService.validate(argument)
        }
        return argument

    }

    private fun mapHandlerParameter(parameter: KParameter): HandlerMethodParameter {
        val annotations = parameter.annotations
        mappers.values
            .filter { it.supportsAnnotation(annotations) }
            .forEach { return it.mapParameter(parameter, annotations) }
        throw IllegalArgumentException("Unknown annotated parameter: ${parameter.name}")
    }

    fun mapHandlerParameters(function: KFunction<*>): List<HandlerMethodParameter> {
        val parameters = mutableListOf<HandlerMethodParameter>()
        for (parameter in function.valueParameters) {
            if (parameter.annotations.isEmpty()) {
                parameters.add(HandlerMethodParameter(parameter.name!!, parameter.type.javaType as Class<*>,
                    parameter.type.isMarkedNullable))
            } else {
                parameters.add(mapHandlerParameter(parameter))
            }
        }
        return parameters
    }

    fun init() {
        mappers = componentsStorage.getParameterMappers().associateBy { it.getSupportedAnnotation().simpleName!! }
    }

}