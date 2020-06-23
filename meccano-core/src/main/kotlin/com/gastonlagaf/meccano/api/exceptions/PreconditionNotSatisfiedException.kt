package com.gastonlagaf.meccano.api.exceptions

class PreconditionNotSatisfiedException(
    message: String,
    val viewLogin: Boolean = false
) : RuntimeException(message)