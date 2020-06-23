package com.gastonlagaf.meccano.api

interface HttpHeaders {

    fun add(name: String, vararg value: String)

    fun getOne(name: String): String?

    fun getAll(name: String): List<String>

    fun getAll(): Map<String, List<String>>

    fun remove(name: String, value: String? = null)
}