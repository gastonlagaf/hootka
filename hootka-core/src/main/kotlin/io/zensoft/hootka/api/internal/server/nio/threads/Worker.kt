package io.zensoft.hootka.api.internal.server.nio.threads

import java.nio.channels.SelectableChannel

interface Worker: Runnable {

    fun initialize()

    fun accept(selectableChannel: SelectableChannel)

    fun shutdown()

}