package com.example.study.infra.redis

import java.time.Duration

data class DistributedLockKey(
    val lockType: DistributedLockType,
    val key: String,
    val ttlDuration: Duration,
) : RedisKey() {

    override fun subKey(): String {
        return "${lockType.prefix}:$key"
    }

    override fun ttlDuration(): Duration = ttlDuration

    companion object {
        fun of(
            lockType: DistributedLockType,
            key: String
        ): DistributedLockKey {
            return DistributedLockKey(
                lockType = lockType,
                key = key,
                ttlDuration = Duration.ofSeconds(10),
            )
        }

        fun makeCreateOrderKey(orderId: Long, orderItemKeys: List<Long>): String {
            return "p:${orderId}:${orderItemKeys.joinToString(",")}"
        }

        fun makeCancelOrderKey(orderId: Long, paymentUUID: String): String {
            return "pc:${orderId}:$paymentUUID}"
        }
    }
}