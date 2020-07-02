package com.gastonlagaf.meccano.api.internal.path


class DefaultPathMatcher : PathMatcher {

    override fun getPathVariables(template: String, actualPath: String): Map<String, String> {
        val templateDirs = template.split(PATH_SEPARATOR)
        val pathDirs = actualPath.split(PATH_SEPARATOR)
        val variables: MutableMap<String, String> = mutableMapOf()

        require(matchString(template, actualPath) && templateDirs.size == pathDirs.size) { return (mapOf()) }

        templateDirs.forEachIndexed { idx, it ->
            if (DOUBLE_STARS == it) return mapOf()

            if (it.startsWith('{') && it.endsWith('}')) {
                val name = it.substring(1, it.length - 1)
                val value = pathDirs[idx]
                variables[name] = value
            }
        }

        return variables
    }

    override fun matches(template: String, actualPath: String): Boolean {
        if (actualPath.startsWith(PATH_SEPARATOR) != template.startsWith(PATH_SEPARATOR)) {
            return false
        }

        val templateDirs = template.split(PATH_SEPARATOR)
        val pathDirs = actualPath.split(PATH_SEPARATOR)

        var pathIdxStart = 0
        var templateIdxStart = 0
        var pathIdxEnd = pathDirs.size - 1
        var templateIdxEnd = templateDirs.size - 1


        // Match all elements up to the first **
        // path:      -->  /.../.../...
        // template:  -->  /.../**/...   OR /.../.../...
        while (pathIdxStart <= pathIdxEnd && templateIdxStart <= templateIdxEnd) {
            val pathDir = pathDirs[pathIdxStart]
            val templateDir = templateDirs[templateIdxStart]

            if (DOUBLE_STARS == templateDir) {
                break
            }

            if (!matchString(templateDir, pathDir)) {
                return false
            }
            pathIdxStart++
            templateIdxStart++
        }

        if (pathIdxStart > pathIdxEnd) {
            if (templateIdxStart > templateIdxEnd) {
                // path == template
                // path:      /.../.../...
                // template:  /.../.../...
                return true
            }
            if (templateIdxStart == templateIdxEnd && DOUBLE_STARS == templateDirs[templateIdxStart]) {
                // path:  /...
                // template -> "/.../**"
                return true
            }
        } else if (templateIdxStart > templateIdxEnd) {
            // Failure
            return false
        } else if (DOUBLE_STARS == templateDirs[templateIdxStart] && templateIdxStart == templateIdxEnd) {
            // path:  /.../... OR /.../.../...(N)
            // template -> "/.../**"
            return true
        }

        // up to last '**'
        // path:      /.../.../...  <--
        // template:  /.../**/...   <--
        while (pathIdxStart <= pathIdxEnd && templateIdxStart <= templateIdxEnd) {
            val pathDir = pathDirs[pathIdxEnd]
            val templateDir = templateDirs[templateIdxEnd]

            if (DOUBLE_STARS == templateDir) {
                break
            }

            if (!matchString(templateDir, pathDir)) {
                return false
            }
            pathIdxEnd--
            templateIdxEnd--
        }

        if (pathIdxStart > pathIdxEnd) {
            for (i in templateIdxStart..templateIdxEnd) {
                if (DOUBLE_STARS != templateDirs[i]) {
                    return false
                }
            }
            return true
        }

        // template:   ..../**/ --> .. .. /**/....
        while (templateIdxStart != templateIdxEnd && pathIdxStart <= pathIdxEnd) {
            var templIdxTmp = -1
            for (i in (templateIdxStart + 1)..templateIdxEnd) {
                if (DOUBLE_STARS == templateDirs[i]) {
                    templIdxTmp = i
                    break
                }
            }
            if (templIdxTmp == templateIdxStart + 1) { // '**/**' situation, so skip one
                templateIdxStart++
                continue
            }

            val templLength: Int = templIdxTmp - templateIdxStart - 1
            val pathStrLength = pathIdxEnd - pathIdxStart + 1
            var foundIdx = -1

            strLoop@ for (i in 0..pathStrLength - templLength) {
                for (j in 0 until templLength) {
                    val subTempl: String = templateDirs[templateIdxStart + j + 1]
                    val subPathStr = pathDirs[pathIdxStart + i + j]
                    if (!matchString(subTempl, subPathStr)) {
                        continue@strLoop
                    }
                }
                foundIdx = pathIdxStart + i
                break
            }

            if (foundIdx == -1) {
                return false
            }

            templateIdxStart = templIdxTmp
            pathIdxStart = foundIdx + templLength
        }

        // template: .... -> /**(N)  OR -> FALSE
        for (i in templateIdxStart..templateIdxEnd) {
            if (DOUBLE_STARS != templateDirs[i]) {
                return false
            }
        }

        return true
    }


    override fun getPath(original: String, target: String): String {
        require(matches(target, original)) { return "" }
        val pathParts = original.split('/').toMutableList()
        val targetParts = target.split('/')

        targetParts.forEachIndexed { idx, it ->
            if (STAR == it) {
                return pathParts[idx]
            } else if (DOUBLE_STARS == it) {
                return pathParts.joinToString(separator = "/")
            }
            pathParts.removeAt(0)
        }
        return ""
    }

    private fun matchString(pattern: String, str: String): Boolean {
        return if (str == pattern) {
            true
        } else (pattern.startsWith("{") && pattern.endsWith("}")) || pattern.startsWith("*")
    }

    companion object {
        const val PATH_SEPARATOR = "/"
        const val STAR = "*"
        const val DOUBLE_STARS = "**"
    }

}