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

    @Test
    fun matchesWildcardTest() {
        val path = "/api/test/test1/test2/test3/test4/me"
        val template = "/api/**/test2/**/me"
        Assert.assertTrue(pathMatcher.matches(template, path))
    }

    @Test
    fun getPathVariablesTest() {
        val path = "/api/test1/1/test2/2/me"
        val template = "/api/test1/{var1}/test2/{var2}/me"
        val expectedResult = mapOf("var1" to "1", "var2" to "2")
        val actualResult = pathMatcher.getPathVariables(template, path)
        Assert.assertEquals(expectedResult.size, actualResult.size)
        actualResult.forEach { (key, value) ->
            Assert.assertEquals(expectedResult[key], value)
        }
    }

    @Test
    fun getPathTest() {
        val path = "/docs/cvs/commit.css"
        val template = "/docs/**"

        val expectedResult = "cvs/commit.css"
        val actualResult = pathMatcher.getPath(path, template)
        Assert.assertEquals(expectedResult, actualResult)
    }

}