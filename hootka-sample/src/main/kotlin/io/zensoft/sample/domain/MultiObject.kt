package io.zensoft.sample.domain

import io.zensoft.hootka.api.support.InMemoryFile

data class MultiObject(
    val file: InMemoryFile,
    val description: String
)