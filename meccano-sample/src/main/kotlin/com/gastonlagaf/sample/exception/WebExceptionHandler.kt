package com.gastonlagaf.sample.exception

import com.gastonlagaf.meccano.annotation.ControllerAdvice
import com.gastonlagaf.meccano.annotation.ExceptionHandler
import com.gastonlagaf.meccano.annotation.ResponseStatus
import com.gastonlagaf.meccano.api.model.HttpResponseStatus

@ControllerAdvice
class WebExceptionHandler {

    @ResponseStatus(HttpResponseStatus.BAD_REQUEST)
    @ExceptionHandler(values = [IllegalArgumentException::class])
    fun handleIllegalArgument(): String {
        return HttpResponseStatus.BAD_REQUEST.name
    }

}