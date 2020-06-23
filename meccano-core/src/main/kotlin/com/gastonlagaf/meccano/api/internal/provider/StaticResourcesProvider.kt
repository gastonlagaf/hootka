package com.gastonlagaf.meccano.api.internal.provider

import com.gastonlagaf.meccano.api.StaticResourceHandler
import com.gastonlagaf.meccano.api.WrappedHttpRequest
import com.gastonlagaf.meccano.api.WrappedHttpResponse
import com.gastonlagaf.meccano.api.internal.path.PathMatcher
import com.gastonlagaf.meccano.api.internal.utils.ResourceMimeTypeUtils
import com.gastonlagaf.meccano.api.model.HttpResponseStatus
import com.gastonlagaf.meccano.api.support.HttpHeaderTitles
import com.gastonlagaf.meccano.api.support.HttpMethod
import org.apache.commons.io.IOUtils

class StaticResourcesProvider(
    private val componentsStorage: ComponentsStorage,
    private val resourceMaxAge: Long,
    private val pathMatcher: PathMatcher
) {

    private val resourceProviders = HashMap<String, StaticResourceHandler>()

    fun handleStaticResource(request: WrappedHttpRequest, response: WrappedHttpResponse): Boolean {
        if (HttpMethod.GET == request.getMethod()) {
            val path = request.getPath()
            val resourceHandler = resourceProviders.entries.find { pathMatcher.matches(it.key, path) }?.value
            if (resourceHandler != null) {
                val resourcePath = pathMatcher.getPath(resourceHandler.getPath(), path)
                val resultFile = resourceHandler.findResource(resourcePath)
                if (resultFile != null) {
                    val responseBody = IOUtils.toByteArray(resultFile)
                    response.mutate(HttpResponseStatus.OK, ResourceMimeTypeUtils.resolveMimeType(resourcePath), responseBody)
                    if (resourceHandler.isCacheable()) {
                        response.setHeader(HttpHeaderTitles.cacheControl.value, "max-age=$resourceMaxAge")
                    }
                    return true
                }
            }
        }
        return false
    }

    fun init() {
        val resourceHandlers = componentsStorage.getResourceHandlers()
        resourceHandlers.forEach {
            val path = it.getPath()
            if (!resourceProviders.containsKey(path)) {
                resourceProviders[path] = it
            } else {
                throw IllegalStateException("There is two mappings on static resources with path: $path")
            }
        }
    }

}