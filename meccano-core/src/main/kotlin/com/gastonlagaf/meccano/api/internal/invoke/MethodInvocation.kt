package com.gastonlagaf.meccano.api.internal.invoke

interface MethodInvocation {

    fun invoke(args: Array<Any?>): Any?

}