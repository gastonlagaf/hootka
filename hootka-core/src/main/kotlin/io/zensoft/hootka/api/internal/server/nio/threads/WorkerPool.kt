package io.zensoft.hootka.api.internal.server.nio.threads

import io.zensoft.hootka.api.internal.server.nio.threads.Worker

interface WorkerPool {

    fun initialize()

    fun next(): Worker

    fun shutdown()

}