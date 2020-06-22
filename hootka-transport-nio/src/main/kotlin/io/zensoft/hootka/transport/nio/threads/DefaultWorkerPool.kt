package io.zensoft.hootka.transport.nio.threads

import io.zensoft.hootka.api.RequestProcessor
import io.zensoft.hootka.api.internal.server.nio.threads.Worker
import io.zensoft.hootka.api.internal.server.nio.threads.WorkerPool

class DefaultWorkerPool(
    private val workerCount: Int,
    private val requestProcessor: RequestProcessor
) : WorkerPool {

    @Volatile
    private var index: Int = 0

    private val pool: Array<Worker> = Array(workerCount) { DefaultWorker(256, requestProcessor) }

    override fun initialize() {
        pool.forEach { it.initialize() }
    }

    override fun next(): Worker {
        index = ++index % workerCount
        return pool[index]
    }

    @Synchronized
    override fun shutdown() {
        pool.forEach { it.shutdown() }
    }


}