package com.example.study.common.exception

abstract class AuthException(message: String?, cause: Throwable?): RuntimeException(message, cause) {
    constructor(message: String) : this(message, null)
    constructor(cause: Throwable) : this(null, cause)
    constructor() : this(null, null)
}