package io.zensoft.hootka.api

interface RequestProcessor {

    fun process(wrappedRequest: WrappedHttpRequest, wrappedResponse: WrappedHttpResponse)

}