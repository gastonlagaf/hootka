package com.gastonlagaf.meccano.api.internal.resource

import com.gastonlagaf.meccano.api.StaticResourceHandler
import java.io.InputStream

class ClasspathResourceHandler(
    private val mappedPath: String,
    private val basePath: String,
    private val cacheable: Boolean = true
) : StaticResourceHandler {

    override fun getPath(): String = mappedPath

    override fun findResource(url: String): InputStream? {
        val path = "$basePath/$url"
        return this::class.java.classLoader.getResourceAsStream(path)
    }

    override fun isCacheable(): Boolean = cacheable

}