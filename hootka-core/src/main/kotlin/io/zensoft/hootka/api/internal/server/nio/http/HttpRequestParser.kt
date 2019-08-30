package io.zensoft.hootka.api.internal.server.nio.http


import io.zensoft.hootka.api.internal.server.nio.http.domain.RawHttpRequest
import io.zensoft.hootka.api.model.HttpMethod
import io.zensoft.hootka.api.model.InMemoryFile
import java.io.ByteArrayInputStream

class HttpRequestParser(
        private val string: String
) {

    private var caret: Int = 0
    private var position: CaretPosition = CaretPosition.REQUEST_PATH
    private var emptyParams: Boolean = false


    fun requestPath(request: RawHttpRequest) {
        val method = readUntil(SPACE)
        val path = readPath(PARAM)
        var parameters: String? = null
        if (!emptyParams) {
            parameters = readUntil(SPACE)
        }

        readUntil()
        position = CaretPosition.HEADERS
        request.apply {
            this.method = HttpMethod.valueOf(method.toUpperCase())
            this.params = parameters
            this.path = path
        }
    }

    fun header(): Pair<String, String> {
        val headerName = readUntil(COLON)
        if (headerName.isEmpty()) {
            skip(1)
            position = CaretPosition.BODY
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
                params[paramName]?.addAll(param.split(','))
            } else {
                params[paramName] = param.split(',') as MutableList<String>
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
        readParams(SEPARATOR)
        skip(1)
        readParams(EQ)
        val boundary = readParams(NEW_LINE)

        return boundary
    }

    fun content(): InMemoryFile {
        position = CaretPosition.CONTENT_DISPOSITION
        val fileData = mutableMapOf<String, String>()
        readUntil()
        readUntil(COLON)
        readUntil(SEPARATOR)
        while (CaretPosition.CONTENT_DISPOSITION == position) {
            val name = readContent(EQ).trim()
            val value = readContent(SEPARATOR).trim('"')
            fileData[name.toUpperCase()] = value.trim()
        }
        readUntil(COLON)
        readUntil()
        val file = string.substring(caret)
        return InMemoryFile(fileData["FILENAME"]!!, ByteArrayInputStream(file.toByteArray()))
    }

    fun headersAvailable(): Boolean {
        return CaretPosition.HEADERS == position
    }

    fun readUntil(char: Char = NEW_LINE): String {
        val result = StringBuilder()
        var c = string[caret]
        while (c != char && c != NEW_LINE && !contentRead()) {
            result.append(c)
            c = string[++caret]
        }
        if (char == NEW_LINE) {
            skip(2)
        } else {
            skip(1)
        }
        return result.toString()
    }

    fun readPath(char: Char): String {
        val result = StringBuilder()
        var c = string[caret]
        while (c != char && c != SPACE) {
            result.append(c)
            c = string[++caret]
        }
        if (char == PARAM && c == SPACE) {
            emptyParams = true
        } else {
            skip(1)
        }
        return result.toString()
    }

    fun readParams(char: Char): String {
        val result = StringBuilder()
        var c = string[caret]
        while (c != char && c != NEW_LINE && !contentRead()) {
            result.append(c)
            c = string[++caret]
        }
        if (caret == string.lastIndex) {
            result.append(c)
        }
        skip(1)
        return result.toString()
    }

    fun readContent(char: Char = NEW_LINE): String {
        val result = StringBuilder()
        var c = string[caret]
        while (c != char && c != NEW_LINE && !contentRead()) {
            result.append(c)
            c = string[++caret]
        }
        if (char == NEW_LINE) {
            skip(2)
        }
        if (char == SEPARATOR && c == NEW_LINE) {
            position = CaretPosition.CONTENT_TYPE
            skip(2)
        } else {
            skip(1)
        }
        return result.toString()
    }

    fun contentRead(): Boolean {
        return caret >= string.lastIndex
    }

    private fun skip(chars: Int) {
        caret += chars
    }

    companion object {
        private const val NEW_LINE = '\r'
        private const val COLON = ':'
        private const val SPACE = ' '
        private const val PARAM = '?'
        private const val PARAM_SEPARATOR = '&'
        private const val SEPARATOR = ';'
        private const val EQ = '='
    }

}

enum class CaretPosition {
    REQUEST_PATH,
    HEADERS,
    BODY,
    CONTENT_DISPOSITION,
    CONTENT_TYPE,
    END
}