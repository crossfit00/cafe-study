package com.example.study.infra.redis

import java.time.Duration

abstract class RedisKey {

    open fun keyString(): String = subKey()

    abstract fun subKey(): String

    abstract fun ttlDuration(): Duration?
}