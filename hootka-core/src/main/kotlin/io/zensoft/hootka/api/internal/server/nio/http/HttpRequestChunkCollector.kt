package io.zensoft.hootka.api.internal.server.nio.http

import io.zensoft.hootka.api.WrappedHttpRequest
import java.net.SocketAddress
import java.nio.ByteBuffer

interface HttpRequestChunkCollector {

    fun collect(chunk: ByteBuffer)

    fun aggregate(address: SocketAddress): WrappedHttpRequest

    fun requestRead(): Boolean
}
