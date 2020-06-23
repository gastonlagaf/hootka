package com.gastonlagaf.meccano.transport.nio.http

import com.gastonlagaf.meccano.api.WrappedHttpRequest
import java.net.SocketAddress
import java.nio.ByteBuffer

interface HttpRequestChunkCollector {

    fun collect(chunk: ByteBuffer)

    fun aggregate(address: SocketAddress): WrappedHttpRequest

    fun requestRead(): Boolean
}
