package io.zensoft.hootka.api.internal.server.nio.threads

import io.zensoft.hootka.api.internal.handler.BaseRequestProcessor

class DefaultWorkerPool(
        private val workerCount: Int,
        private val requestProcessor: BaseRequestProcessor
): WorkerPool {

    @Volatile private var index: Int = 0

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