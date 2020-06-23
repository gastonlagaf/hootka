package com.gastonlagaf.meccano.api

import com.gastonlagaf.meccano.api.internal.security.RootSecurityExpressions

interface SecurityExpressionInitializer {

    fun createSecurityExpressions(principal: UserDetails?): RootSecurityExpressions

}