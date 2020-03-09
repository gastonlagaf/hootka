package io.zensoft.sample.exception

import io.zensoft.hootka.annotation.ControllerAdvice
import io.zensoft.hootka.annotation.ExceptionHandler
import io.zensoft.hootka.annotation.ResponseStatus
import io.zensoft.hootka.api.model.HttpResponseStatus

@ControllerAdvice
class WebExceptionHandler {

    @ResponseStatus(HttpResponseStatus.BAD_REQUEST)
    @ExceptionHandler(values = [IllegalArgumentException::class])
    fun handleIllegalArgument(): String {
        return HttpResponseStatus.BAD_REQUEST.name
    }

}