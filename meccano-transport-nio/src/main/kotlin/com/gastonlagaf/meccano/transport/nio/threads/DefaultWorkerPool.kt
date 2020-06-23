package com.gastonlagaf.meccano.transport.nio.threads

import com.gastonlagaf.meccano.api.RequestProcessor

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