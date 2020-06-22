package io.zensoft.hootka.api.internal.server.nio.threads

interface WorkerPool {

    fun initialize()

    fun next(): Worker

    fun shutdown()

}