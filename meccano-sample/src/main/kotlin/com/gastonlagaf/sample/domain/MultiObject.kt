package com.gastonlagaf.sample.domain

import com.gastonlagaf.meccano.api.support.InMemoryFile

data class MultiObject(
    val file: InMemoryFile,
    val description: String
)