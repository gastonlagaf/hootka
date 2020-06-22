package io.zensoft.hootka.api.internal.mapper

import io.zensoft.hootka.annotation.MultipartObject
import io.zensoft.hootka.api.HttpRequestMapper
import io.zensoft.hootka.api.internal.CaretPosition
import io.zensoft.hootka.api.internal.HttpRequestParser
import io.zensoft.hootka.api.internal.support.HandlerMethodParameter
import io.zensoft.hootka.api.internal.support.HttpHandlerMetaInfo
import io.zensoft.hootka.api.internal.support.RequestContext
import io.zensoft.hootka.api.internal.utils.NumberUtils
import io.zensoft.hootka.api.support.HttpHeaderTitles
import io.zensoft.hootka.api.support.InMemoryFile
import java.nio.charset.Charset
import javax.validation.Valid
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaConstructor
import kotlin.reflect.jvm.javaType

class DefaultMultipartObjectMapper : HttpRequestMapper {

    override fun getSupportedAnnotation(): KClass<out Annotation> = MultipartObject::class

    override fun supportsAnnotation(annotations: List<Annotation>): Boolean {
        return annotations.find { it is MultipartObject } != null
    }

    override fun createValue(parameter: HandlerMethodParameter, context: RequestContext, handlerMethod: HttpHandlerMetaInfo): Any? {
        val multipartContent = context.request.getContentAsString(Charset.defaultCharset())
        val annotation = parameter.annotation!! as MultipartObject
        val ctor = parameter.clazz.kotlin.primaryConstructor!!
        val args = mutableListOf<Any?>()

        val boundary = context.request.getHeader(HttpHeaderTitles.contentType.uppercasedValue)
            ?.let { HttpRequestParser(it, CaretPosition.HEADERS).boundary() }
            ?: throw IllegalArgumentException("Content-Type spec not found for multipart request")
        val splitBody = multipartContent.split(boundary)
        val objectList = splitBody.subList(1, splitBody.lastIndex)
        val contentList = mutableMapOf<String?, Any?>()
        objectList.forEach {
            val pair = HttpRequestParser(it, CaretPosition.BODY).multipartObject()
            contentList[pair.first] = pair.second
        }
        for (field in ctor.parameters) {
            val content = contentList[field.name]
            if (null == content) {
                args.add(null)
                continue
            }
            args.add(createFieldValue(content, annotation, field))
        }
        val javaCtor = ctor.javaConstructor
        return javaCtor!!.newInstance(*args.toTypedArray())
    }

    override fun mapParameter(parameter: KParameter, annotations: List<Annotation>): HandlerMethodParameter {
        val annotation = annotations.find { it is MultipartObject }
        val validationRequired = annotations.find { it is Valid } != null
        return HandlerMethodParameter(parameter.name!!, parameter.type.javaType as Class<*>,
            parameter.type.isMarkedNullable, annotation, validationRequired)
    }

    private fun extractExtension(fileName: String): String = fileName.substring(fileName.lastIndexOf('.') + 1)

    private fun createFieldValue(data: Any, annotation: MultipartObject, field: KParameter): Any {
        return when (field.type.javaType as Class<*>) {
            InMemoryFile::class.java -> {
                val fileUpload = data as InMemoryFile
                val extension = extractExtension(fileUpload.name)
                if (annotation.acceptExtensions.isNotEmpty() && !annotation.acceptExtensions.contains(extension)) {
                    throw IllegalArgumentException("Unsupported file type with extension $extension")
                }
                fileUpload
            }
            else -> {
                val attribute = data as String
                val parameterType = field.type.javaType as Class<*>
                if (parameterType.isEnum) {
                    parameterType.getDeclaredMethod("valueOf", String::class.java).invoke(null, attribute.toUpperCase())
                } else {
                    NumberUtils.parseNumber(attribute, parameterType)
                }
            }
        }
    }
}