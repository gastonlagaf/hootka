package com.gastonlagaf.meccano.api.internal.path

import org.junit.Assert
import org.junit.Test

class DefaultPathMatcherTests {

    private val pathMatcher: PathMatcher = DefaultPathMatcher()

    @Test
    fun matchesRawTest() {
        val path = "/api/test/me"
        val template = "/api/test/me"
        Assert.assertTrue(pathMatcher.matches(template, path))
    }

    @Test
    fun matchesRawTestFails() {
        val path = "/api/test/me"
        val template = "/api/test/mee"
        Assert.assertFalse(pathMatcher.matches(template, path))
    }

    @Test
    fun matchesVariableTest() {
        val path = "/api/test/me"
        val template = "/api/{mock}/me"
        Assert.assertTrue(pathMatcher.matches(template, path))
    }

    fun matchesWildcardTest() {

    }

    fun getPathVarialbesTest() {

    }

    fun getPathTest() {

    }

}