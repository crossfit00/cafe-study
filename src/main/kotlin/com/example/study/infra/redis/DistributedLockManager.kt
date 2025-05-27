package com.example.study.infra.redis

import com.example.study.common.exception.ApiException
import org.springframework.stereotype.Component

@Component
class DistributedLockManager(
    private val distributedLockRedisRepository: DistributedLockRedisRepository
) {

    fun <T> executeWithLock(
        lockKey: DistributedLockKey,
        failAcquireLockException: ApiException,
        execute: () -> T,
    ): T {
        if (!distributedLockRedisRepository.tryLock(key = lockKey)) {
            throw failAcquireLockException
        }

        return try {
            execute()
        } finally {
            distributedLockRedisRepository.unLock(lockKey)
        }
    }
}