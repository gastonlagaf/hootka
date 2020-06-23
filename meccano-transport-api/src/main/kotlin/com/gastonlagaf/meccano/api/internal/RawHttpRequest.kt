package com.gastonlagaf.meccano.api.internal

import com.gastonlagaf.meccano.api.support.HttpMethod
import java.net.SocketAddress

class RawHttpRequest(
    var path: String? = null,
    var method: HttpMethod? = null,
    var params: String? = null,
    var content: ByteArray? = null,
    var headers: Map<String, String>? = null,
    var socketAddress: SocketAddress? = null
)
