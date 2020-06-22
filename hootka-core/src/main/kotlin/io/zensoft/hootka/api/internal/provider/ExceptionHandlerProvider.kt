package io.zensoft.hootka.api.internal.provider

import io.zensoft.hootka.annotation.ExceptionHandler
import io.zensoft.hootka.annotation.ResponseStatus
import io.zensoft.hootka.api.internal.invoke.MethodInvocationProducer
import io.zensoft.hootka.api.internal.support.ExceptionHandlerKey
import io.zensoft.hootka.api.internal.support.HttpHandlerMetaInfo
import io.zensoft.hootka.api.model.HttpMethod
import io.zensoft.hootka.api.model.HttpResponseStatus
import io.zensoft.hootka.api.model.MimeType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation

class ExceptionHandlerProvider(
    private val componentsStorage: ComponentsStorage,
    private val handlerParameterMapperProvider: HandlerParameterMapperProvider,
    private val methodInvocationProducer: MethodInvocationProducer = MethodInvocationProducer()
) {

    private val exceptionHandlers = HashMap<ExceptionHandlerKey, HttpHandlerMetaInfo>()


    fun getExceptionHandler(exceptionType: KClass<out Throwable>, contentType: MimeType = MimeType.APPLICATION_JSON): HttpHandlerMetaInfo? {
        val key = ExceptionHandlerKey(exceptionType, contentType.toString())
        return exceptionHandlers[key]
    }

    fun init() {
        val advices = componentsStorage.getExceptionHandlers()
        for (advice in advices) {
            for (function in advice::class.declaredFunctions) {
                val annotation = function.findAnnotation<ExceptionHandler>() ?: continue
                val parameterMapping = handlerParameterMapperProvider.mapHandlerParameters(function)
                val status = function.findAnnotation<ResponseStatus>()?.value ?: HttpResponseStatus.OK
                val methodInvocation = methodInvocationProducer.generateMethodInvocation(advice, function, parameterMapping)
                val handlerMetaInfo = HttpHandlerMetaInfo(advice, methodInvocation, parameterMapping,
                    false, status, annotation.produces, "", HttpMethod.GET, null)
                for (exceptionType in annotation.values) {
                    val key = ExceptionHandlerKey(exceptionType, annotation.produces.toString())
                    if (exceptionHandlers.containsKey(key)) {
                        throw IllegalStateException("Only one handler should be applied on $exceptionType")
                    }
                    exceptionHandlers[key] = handlerMetaInfo
                    log.info("Registered ${function.name}, handling ${exceptionType.simpleName}")
                }
            }
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ExceptionHandlerProvider::class.java)
    }
}