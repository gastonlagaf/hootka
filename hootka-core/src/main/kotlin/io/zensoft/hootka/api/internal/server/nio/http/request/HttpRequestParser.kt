package io.zensoft.hootka.api.internal.server.nio.http.request

import io.zensoft.hootka.api.internal.exception.InvalidCaretLocationException
import io.zensoft.hootka.api.internal.server.nio.http.domain.RawHttpRequest
import io.zensoft.hootka.api.internal.server.nio.http.request.CaretPosition.*
import io.zensoft.hootka.api.model.HttpMethod
import io.zensoft.hootka.api.model.InMemoryFile
import java.io.ByteArrayInputStream

class HttpRequestParser(
    private val string: String,
    private var position: CaretPosition = REQUEST_PATH
) {

    private var caret: Int = 0
    private var emptyParams: Boolean = false


    fun requestPath(request: RawHttpRequest) {
        requirePosition(REQUEST_PATH)

        val method = readUntil(SPACE)
        val path = readPath()
        var parameters: String? = null
        if (!emptyParams) {
            parameters = readUntil(SPACE)
        }

        readUntil()
        position = HEADERS
        request.apply {
            this.method = HttpMethod.valueOf(method.toUpperCase())
            this.params = parameters
            this.path = path
        }
    }

    fun header(): Pair<String, String> {
        requirePosition(HEADERS)

        val headerName = readUntil(COLON)
        if (headerName.isEmpty()) {
            skip(1)
            position = BODY
            return Pair("", "")
        }
        val headerValue = readUntil()
        return Pair(headerName, headerValue)
    }

    fun params(): Map<String, List<String>> {
        val params = mutableMapOf<String, MutableList<String>>()
        while (caret < string.length) {
            val paramName = readParams(EQ)
            val param = readParams(PARAM_SEPARATOR)
            if (params.contains(paramName)) {
                params[paramName]?.addAll(param.split(COMMA))
            } else {
                params[paramName] = param.split(COMMA) as MutableList<String>
            }
        }
        return params
    }

    fun cookie(): Map<String, String> {
        val cookie = mutableMapOf<String, String>()
        while (caret < string.length) {
            val cookieKey = readParams(EQ)
            val cookieValue = readParams(SEPARATOR)
            skip(1)
            cookie[cookieKey] = cookieValue
        }
        return cookie
    }

    fun boundary(): String {
        requirePosition(HEADERS)

        readParams(EQ)
        return BOUNDARY_DELIMITER.plus(readParams(NEW_LINE)) // RFC 1341, paragraph 7.2.1
    }

    fun multipartFile(): InMemoryFile {
        requirePosition(BODY)
        val fileData = extractMultipartMeta()
        if (CONTENT_TYPE == position) {
            readUntil()
            skip(CRLF.length)
        }
        val file = string.substring(caret).removeSuffix(LINE_SKIP)
        return InMemoryFile(fileData.getValue("FILENAME"), ByteArrayInputStream(file.toByteArray()))
    }

    fun multipartObject(): Pair<String?, Any?> {
        requirePosition(BODY)

        val fileData = extractMultipartMeta()
        readContent(COLON)
        if (CONTENT_TYPE == position) {
            readUntil()
            skip(2)
            val file = string.substring(caret).removeSuffix(LINE_SKIP)
            return Pair(fileData["NAME"], InMemoryFile(fileData.getValue("FILENAME"), ByteArrayInputStream(file.toByteArray())))
        }
        val text = string.substring(caret).removeSuffix(CRLF)
        return Pair(fileData["NAME"], text)
    }

    fun headersAvailable(): Boolean {
        return HEADERS == position
    }

    fun readUntil(char: Char = NEW_LINE): String {
        val result = StringBuilder()
        var c = string[caret]
        while (c != char && c != NEW_LINE && !contentRead()) {
            result.append(c)
            c = string[++caret]
        }
        if (char == NEW_LINE) {
            skip(CRLF.length)
        } else {
            skip(ONE_SYMBOL)
        }
        return result.toString()
    }

    fun contentRead(): Boolean {
        return caret >= string.lastIndex
    }

    private fun extractMultipartMeta(): Map<String, String> {
        position = CONTENT_DISPOSITION
        val fileData = mutableMapOf<String, String>()
        readUntil()
        readUntil(COLON)
        readUntil(SEPARATOR)
        while (CONTENT_DISPOSITION == position) {
            val name = readContent(EQ).trim()
            val value = readContent(SEPARATOR).trim(QUOTE)
            fileData[name.toUpperCase()] = value.trim()
        }
        readContent(COLON)
        return fileData
    }

    private fun readPath(char: Char = PARAM): String {
        val result = StringBuilder()
        var c = string[caret]
        while (c != char && c != SPACE) {
            result.append(c)
            c = string[++caret]
        }
        if (char == PARAM && c == SPACE) {
            emptyParams = true
        } else {
            skip(ONE_SYMBOL)
        }
        return result.toString()
    }

    private fun readParams(char: Char): String {
        val result = StringBuilder()
        var c = string[caret]
        while (c != char && c != NEW_LINE && !contentRead()) {
            result.append(c)
            c = string[++caret]
        }
        if (caret == string.lastIndex) {
            result.append(c)
        }
        skip(ONE_SYMBOL)
        return result.toString()
    }

    private fun readContent(char: Char = NEW_LINE): String {
        val result = StringBuilder()
        var c = string[caret]
        while (c != char && c != NEW_LINE && !contentRead()) {
            result.append(c)
            c = string[++caret]
        }
        if (char == SEPARATOR && c == NEW_LINE) {
            position = CONTENT_TYPE
            skip(ONE_SYMBOL)
        }
        if (char == COLON && c == NEW_LINE) {
            position = BODY
            skip(CRLF.length)
        } else {
            skip(ONE_SYMBOL)
        }
        return result.toString()
    }

    private fun skip(chars: Int) {
        caret += chars
    }

    private fun requirePosition(requiredPosition: CaretPosition) {
        if (position != requiredPosition) {
            throw InvalidCaretLocationException(position, requiredPosition)
        }
    }

    companion object {
        private const val NEW_LINE = '\r'
        private const val COLON = ':'
        private const val SPACE = ' '
        private const val PARAM = '?'
        private const val PARAM_SEPARATOR = '&'
        private const val SEPARATOR = ';'
        private const val EQ = '='
        private const val COMMA = ','
        private const val QUOTE = '"'

        private const val ONE_SYMBOL = 1;
        
        private const val BOUNDARY_DELIMITER = "--"
        private const val CRLF = "\r\n"
        private const val LINE_SKIP = "$CRLF$CRLF"
    }

}