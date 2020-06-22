package io.zensoft.hootka.api.support

import io.zensoft.hootka.api.HttpHeaders

class DefaultHttpHeaders(
    val values: MutableMap<String, MutableList<String>> = mutableMapOf()
): HttpHeaders {

    override fun add(name: String, vararg value: String) {
        val valuesList = values.getOrPut(name) { mutableListOf() }
        valuesList.addAll(value)
    }

    override fun getOne(name: String): String? {
        return values[name]?.first()
    }

    override fun getAll(name: String): List<String> {
        return values[name] ?: emptyList()
    }

    override fun getAll(): Map<String, List<String>> {
        return values
    }

    override fun remove(name: String, value: String?) {
        if (null == value) {
            values.remove(name)
        } else {
            values[name]?.remove(value)
        }
    }

}