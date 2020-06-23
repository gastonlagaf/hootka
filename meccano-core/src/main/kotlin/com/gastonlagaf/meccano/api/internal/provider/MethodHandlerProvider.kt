package com.gastonlagaf.meccano.api.internal.provider

import com.gastonlagaf.meccano.annotation.PreAuthorize
import com.gastonlagaf.meccano.annotation.RequestMapping
import com.gastonlagaf.meccano.annotation.ResponseStatus
import com.gastonlagaf.meccano.annotation.Stateless
import com.gastonlagaf.meccano.api.exceptions.HandlerMethodNotFoundException
import com.gastonlagaf.meccano.api.internal.invoke.MethodInvocationProducer
import com.gastonlagaf.meccano.api.internal.path.PathMatcher
import com.gastonlagaf.meccano.api.internal.support.HandlerMethodKey
import com.gastonlagaf.meccano.api.internal.support.HttpHandlerMetaInfo
import com.gastonlagaf.meccano.api.model.HttpResponseStatus
import com.gastonlagaf.meccano.api.support.HttpMethod
import org.apache.commons.lang3.StringUtils
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation

class MethodHandlerProvider(
    private val componentsStorage: ComponentsStorage,
    private val handlerParameterMapperProvider: HandlerParameterMapperProvider,
    private val pathMatcher: PathMatcher
) {

    private val methodInvocationProducer = MethodInvocationProducer()

    private val storage = hashMapOf<HandlerMethodKey, HttpHandlerMetaInfo>()

    fun getMethodHandler(path: String, httpMethod: HttpMethod): HttpHandlerMetaInfo {
        val stringMethod = httpMethod.toString()
        return storage.entries
            .firstOrNull { it.key.method == stringMethod && pathMatcher.matches(it.key.path, path) }?.value
            ?: throw HandlerMethodNotFoundException("$httpMethod $path - Not Found")
    }

    fun init() {
        val beans = componentsStorage.getMethodHandlers()
        for (bean in beans) {
            val superPath = bean::class.findAnnotation<RequestMapping>()?.value?.single() ?: StringUtils.EMPTY
            val globalPrecondition = bean::class.findAnnotation<PreAuthorize>()
            val statelessBeanAnnotation = bean::class.findAnnotation<Stateless>()
            val functions = bean::class.declaredFunctions
            for (function in functions) {
                val pathAnnotation = function.findAnnotation<RequestMapping>() ?: continue
                val parameterMapping = handlerParameterMapperProvider.mapHandlerParameters(function)
                val status = function.findAnnotation<ResponseStatus>()?.value ?: HttpResponseStatus.OK
                val type = pathAnnotation.produces
                val stateless = statelessBeanAnnotation != null || function.findAnnotation<Stateless>() != null
                val preconditionExpression = resolveAllowancePrecondition(globalPrecondition, function)
                val methodInvocation = methodInvocationProducer.generateMethodInvocation(bean, function, parameterMapping)
                val paths = if (pathAnnotation.value.isNotEmpty()) {
                    pathAnnotation.value.map { superPath + it }
                } else {
                    listOf(superPath)
                }
                for (path in paths) {
                    val key = HandlerMethodKey(path, pathAnnotation.method.toString())
                    if (storage.containsKey(key)) {
                        throw IllegalStateException("Mapping $path is already exists.")
                    } else {
                        storage[key] = HttpHandlerMetaInfo(bean, methodInvocation, parameterMapping,
                            stateless, status, type, path, pathAnnotation.method, preconditionExpression)
                    }
                }
            }
        }
    }

    private fun resolveAllowancePrecondition(globalPrecondition: PreAuthorize?, function: KFunction<*>): String? {
        val methodPrecondition = function.findAnnotation<PreAuthorize>()
        return methodPrecondition?.value ?: globalPrecondition?.value
    }

}