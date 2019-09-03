package io.zensoft.hootka.api.internal.mapper

import io.zensoft.hootka.annotation.MultipartFile
import io.zensoft.hootka.api.HttpRequestMapper
import io.zensoft.hootka.api.internal.server.nio.http.HttpRequestParser
import io.zensoft.hootka.api.internal.support.HandlerMethodParameter
import io.zensoft.hootka.api.internal.support.HttpHandlerMetaInfo
import io.zensoft.hootka.api.internal.support.RequestContext
import io.zensoft.hootka.api.model.InMemoryFile
import java.nio.charset.Charset
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

class DefaultMultipartFileMapper : HttpRequestMapper {

    override fun getSupportedAnnotation(): KClass<out Annotation> = MultipartFile::class

    override fun supportsAnnotation(annotations: List<Annotation>): Boolean {
        return annotations.find { it is MultipartFile } != null
    }

    override fun createValue(parameter: HandlerMethodParameter, context: RequestContext, handlerMethod: HttpHandlerMetaInfo): Any? {
        val multipartContent = context.request.getContentAsString(Charset.defaultCharset())
        val annotation = parameter.annotation!! as MultipartFile

        val boundary = HttpRequestParser(context.request.getHeader("CONTENT-TYPE")!!).boundary()
        val filesList = multipartContent.split(boundary).filter { !it.isBlank() && it != "--\r\n" }
        val files = mutableListOf<InMemoryFile>()
        filesList.forEach {
            val file = HttpRequestParser(it).multipartFile()
            val extension = extractExtension(file.name)
            if (annotation.acceptExtensions.isNotEmpty() && !annotation.acceptExtensions.contains(extension)) {
                throw IllegalArgumentException("Unsupported file type with extension $extension")
            }
            files.add(file)
        }
        return if (parameter.clazz.isArray) {
            return files.toTypedArray()
        } else {
            files.first()
        }
    }

    override fun mapParameter(parameter: KParameter, annotations: List<Annotation>): HandlerMethodParameter {
        val annotation = annotations.find { it is MultipartFile }
        return HandlerMethodParameter(parameter.name!!, parameter.type.javaType as Class<*>,
                parameter.type.isMarkedNullable, annotation)
    }

    private fun extractExtension(fileName: String): String = fileName.substring(fileName.lastIndexOf('.') + 1)

}