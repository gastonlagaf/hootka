package com.gastonlagaf.sample.domain

import com.gastonlagaf.meccano.api.UserAuthority

class Role(
    var key: String
) : UserAuthority {
    override fun getAuthority(): String = key
}