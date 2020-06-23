package com.gastonlagaf.meccano.api.internal.response

import com.gastonlagaf.meccano.api.HttpResponseResolver
import com.gastonlagaf.meccano.api.WrappedHttpResponse
import com.gastonlagaf.meccano.api.model.MimeType
import com.gastonlagaf.meccano.api.model.ViewModel
import freemarker.template.Configuration
import java.io.StringWriter

class FreemarkerResponseResolver(
    private var freemarkerConfig: Configuration
) : HttpResponseResolver {

    override fun getContentType(): MimeType = MimeType.TEXT_HTML

    override fun resolveResponseBody(result: Any, handlerArgs: Array<Any?>, response: WrappedHttpResponse): ByteArray {
        if (result !is String) throw IllegalArgumentException("String return type should be for html view response methods")
        val viewModel = handlerArgs.find { it != null && it is ViewModel } as? ViewModel
            ?: ViewModel()
        val template = freemarkerConfig.getTemplate(result)
        val out = StringWriter()
        val attributes = viewModel.getAttributes()
        template.process(attributes, out)
        return out.toString().toByteArray()
    }

}