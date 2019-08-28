package io.zensoft.hootka.api.internal.server.nio.http.domain


import io.zensoft.hootka.api.model.HttpMethod
import java.net.SocketAddress

class RawHttpRequest(
        var path: String? = null,
        var method: HttpMethod? = null,
        var params: String? = null,
        var content: ByteArray? = null,
        var headers: Map<String, String>? = null,
        var socketAddress: SocketAddress? = null
)
