package io.zensoft.hootka.api.support

import java.io.InputStream

class InMemoryFile(
    val name: String,
    val stream: InputStream
)