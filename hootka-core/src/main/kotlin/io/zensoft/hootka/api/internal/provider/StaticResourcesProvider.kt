package io.zensoft.hootka.api.internal.provider

import io.zensoft.hootka.api.StaticResourceHandler
import io.zensoft.hootka.api.WrappedHttpRequest
import io.zensoft.hootka.api.WrappedHttpResponse
import io.zensoft.hootka.api.internal.utils.ResourceMimeTypeUtils
import io.zensoft.hootka.api.model.HttpMethod
import io.zensoft.hootka.api.model.HttpResponseStatus
import org.apache.commons.io.IOUtils
import org.springframework.context.ApplicationContext
import org.springframework.util.AntPathMatcher
import javax.annotation.PostConstruct

class StaticResourcesProvider(
    private val applicationContext: ApplicationContext,
    private val resourceMaxAge: Long
) {

    companion object {
        private const val CACHE_CONTROL_HEADER = "Cache-Control"
    }

    private val pathMatcher = AntPathMatcher()
    private val resourceProviders = HashMap<String, StaticResourceHandler>()

    fun handleStaticResource(request: WrappedHttpRequest, response: WrappedHttpResponse): Boolean {
        if (HttpMethod.GET == request.getMethod()) {
            val path = request.getPath()
            val resourceHandler = resourceProviders.entries.find { pathMatcher.match(it.key, path) }?.value
            if (resourceHandler != null) {
                val resourcePath = pathMatcher.extractPathWithinPattern(resourceHandler.getPath(), path)
                val resultFile = resourceHandler.findResource(resourcePath)
                if (resultFile != null) {
                    val responseBody = IOUtils.toByteArray(resultFile)
                    response.mutate(HttpResponseStatus.OK, ResourceMimeTypeUtils.resolveMimeType(resourcePath), responseBody)
                    if (resourceHandler.isCacheable()) {
                        response.setHeader(CACHE_CONTROL_HEADER, "max-age=$resourceMaxAge")
                    }
                    return true
                }
            }
        }
        return false
    }

    @PostConstruct
    private fun init() {
        val resourceHandlers = applicationContext.getBeansOfType(StaticResourceHandler::class.java).values
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