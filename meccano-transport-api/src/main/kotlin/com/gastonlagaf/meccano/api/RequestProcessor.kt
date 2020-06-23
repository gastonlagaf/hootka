package com.gastonlagaf.meccano.api

interface RequestProcessor {

    fun process(wrappedRequest: WrappedHttpRequest, wrappedResponse: WrappedHttpResponse)

}