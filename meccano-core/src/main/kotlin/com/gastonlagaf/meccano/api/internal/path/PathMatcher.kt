package com.gastonlagaf.meccano.api.internal.path

interface PathMatcher {

    fun getPathVariables(template: String, actualPath: String): Map<String, String>

    fun matches(template: String, actualPath: String): Boolean

    fun getPath(original: String, target: String): String

}