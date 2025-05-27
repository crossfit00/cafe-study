package com.example.study.infra.redis

import com.example.study.common.exception.ApiException
import com.example.study.common.exception.ErrorCode
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DistributedLockManagerTest {
    private val distributedLockRedisRepository: DistributedLockRedisRepository = mockk()
    private val distributedLockManager: DistributedLockManager = DistributedLockManager(
        distributedLockRedisRepository = distributedLockRedisRepository
    )

    @Test
    fun `분산 락을 선점하고, 해제한다`() {
        every { distributedLockRedisRepository.tryLock(any()) } returns true
        every { distributedLockRedisRepository.unLock(any()) } returns Unit

        distributedLockManager.executeWithLock(
            lockKey = DistributedLockKey.of(
                lockType = DistributedLockType.PAY,
                key = "key"
            ),
            failAcquireLockException = ApiException.from(
                errorCode = ErrorCode.E500_PAYMENT_LOCK_ACQUIRE_FAIL_ERROR,
                resultErrorMessage = "분산락 선점에 실패 했습니다."
            )
        ) {
            LocalDateTime.now()
        }

        verify(exactly = 1) { distributedLockRedisRepository.tryLock(any()) }
        verify(exactly = 1) { distributedLockRedisRepository.unLock(any()) }
    }

    @Test
    fun `실행 도중 에러가 발생해도 락은 해제한다`() {
        every { distributedLockRedisRepository.tryLock(any()) } returns true
        every { distributedLockRedisRepository.unLock(any()) } returns Unit

        val result = runCatching {
            distributedLockManager.executeWithLock(
                lockKey = DistributedLockKey.of(
                    lockType = DistributedLockType.PAY,
                    key = "key"
                ),
                failAcquireLockException = ApiException.from(
                    errorCode = ErrorCode.E500_PAYMENT_LOCK_ACQUIRE_FAIL_ERROR,
                    resultErrorMessage = "분산락 선점에 실패 했습니다."
                )
            ) {
                throw ApiException.from(errorCode = ErrorCode.E500_INTERNAL_SERVER_ERROR)
            }
        }

        assertThat(result.isFailure).isTrue
        assertThat(result.exceptionOrNull()).isInstanceOf(ApiException::class.java)

        verify(exactly = 1) { distributedLockRedisRepository.tryLock(any()) }
        verify(exactly = 1) { distributedLockRedisRepository.unLock(any()) }
    }
}