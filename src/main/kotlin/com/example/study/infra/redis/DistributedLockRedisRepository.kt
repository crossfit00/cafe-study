package com.example.study.infra.redis

import com.example.study.common.code.CommonErrorCode
import com.example.study.common.exception.ApiException
import com.example.study.common.code.ErrorCode
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class DistributedLockRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>
) {

    fun tryLock(key: DistributedLockKey): Boolean {
        val keyString = key.keyString()
        try {
            val operation = redisTemplate.opsForValue()
            return operation.setIfAbsent(keyString, LOCK_DUMMY_VALUE, key.ttlDuration) ?: false
        } catch (throwable: Throwable) {
            throw ApiException.from(
                errorCode = CommonErrorCode.E500_INTERNAL_SERVER_ERROR,
                resultErrorMessage = "락(${key.keyString()})을 획득하는 중 에러가 발생하였습니다 expiredDuration: (${key.ttlDuration})",
                cause = throwable,
            )
        }
    }

    fun unLock(key: DistributedLockKey) {
        val keyString = key.keyString()
        try {
            redisTemplate.delete(keyString)
        } catch (throwable: Throwable) {
            throw ApiException.from(
                errorCode = CommonErrorCode.E500_INTERNAL_SERVER_ERROR,
                resultErrorMessage = "락(${key.keyString()})을 해제하는 중 에러가 발생하였습니다",
                cause = throwable,
            )
        }
    }

    companion object {
        private const val LOCK_DUMMY_VALUE = "D" // 분산 락을 위한 키를 저장할때 저장할 더미 값
    }
}