package com.gastonlagaf.meccano.helper

import com.gastonlagaf.meccano.api.SecurityExpressionInitializer
import com.gastonlagaf.meccano.api.UserDetails
import com.gastonlagaf.meccano.api.internal.security.RootSecurityExpressions

class DefaultSecurityExpressionsInitializer : SecurityExpressionInitializer {

    override fun createSecurityExpressions(principal: UserDetails?): RootSecurityExpressions {
        return RootSecurityExpressions(principal)
    }

}