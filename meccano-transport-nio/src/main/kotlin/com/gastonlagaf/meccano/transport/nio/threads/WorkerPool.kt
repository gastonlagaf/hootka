package com.gastonlagaf.meccano.transport.nio.threads

interface WorkerPool {

    fun initialize()

    fun next(): Worker

    fun shutdown()

}