package com.gastonlagaf.meccano.api

import com.gastonlagaf.meccano.api.internal.support.RequestContext

interface RememberMeService {

    fun createToken(userDetails: UserDetails, response: WrappedHttpResponse): String

    fun invalidateToken(request: WrappedHttpRequest, response: WrappedHttpResponse)

    fun performAutoAuthentication(requestContext: RequestContext): UserDetails?

}