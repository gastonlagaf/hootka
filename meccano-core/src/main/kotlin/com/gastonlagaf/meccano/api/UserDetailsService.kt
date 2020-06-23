package com.gastonlagaf.meccano.api

interface UserDetailsService {

    fun findUserDetailsByUsername(value: String): UserDetails?

}