package com.gastonlagaf.meccano.api.internal.provider

import com.gastonlagaf.meccano.api.HttpRequestMapper
import com.gastonlagaf.meccano.api.HttpResponseResolver
import com.gastonlagaf.meccano.api.StaticResourceHandler

class ComponentsStorage {

    private val methodHandlers: MutableList<Any> = mutableListOf()
    private val exceptionHandlers: MutableList<Any> = mutableListOf()
    private val parameterMappers: MutableList<HttpRequestMapper> = mutableListOf()
    private val responseResolvers: MutableList<HttpResponseResolver> = mutableListOf()
    private val resourceHandlers: MutableList<StaticResourceHandler> = mutableListOf()


    fun addMethodHandlers(list: List<Any>) {
        ensureNotFilled(methodHandlers, "Method Handlers")
        methodHandlers.addAll(list)
    }

    fun addExceptionHandlers(list: List<Any>) {
        ensureNotFilled(exceptionHandlers, "Exception Handlers")
        exceptionHandlers.addAll(list)
    }

    fun addParameterMappers(list: List<HttpRequestMapper>) {
        ensureNotFilled(parameterMappers, "Parameter Mappers")
        parameterMappers.addAll(list)
    }

    fun addResponseResolvers(list: List<HttpResponseResolver>) {
        ensureNotFilled(responseResolvers, "Response Resolvers")
        responseResolvers.addAll(list)
    }

    fun addResourceHandlers(list: List<StaticResourceHandler>) {
        ensureNotFilled(resourceHandlers, "Resource Handlers")
        resourceHandlers.addAll(list)
    }

    fun getMethodHandlers(): List<Any> {
        return methodHandlers
    }

    fun getExceptionHandlers(): List<Any> {
        return exceptionHandlers
    }

    fun getParameterMappers(): List<HttpRequestMapper> {
        return parameterMappers
    }

    fun getResponseResolvers(): List<HttpResponseResolver> {
        return responseResolvers
    }

    fun getResourceHandlers(): List<StaticResourceHandler> {
        return resourceHandlers
    }

    private fun ensureNotFilled(list: List<Any>, componentListName: String) {
        if (list.isNotEmpty()) {
            throw IllegalArgumentException("$componentListName is already filled")
        }
    }


}