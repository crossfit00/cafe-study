package com.example.study.infra.redis

import com.example.study.common.exception.ApiException
import com.example.study.common.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate

@SpringBootTest
class DistributedLockManagerIntegrationTest {

    @Autowired
    private lateinit var distributedLockManager: DistributedLockManager

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Test
    fun `분산 락을 획득하면 키가 생성된다`() {
        distributedLockManager.executeWithLock(
            lockKey = lockKey,
            failAcquireLockException = ApiException.from(ErrorCode.E500_INTERNAL_SERVER_ERROR)
        ) {
            val result = redisTemplate.opsForValue().get(lockKey.keyString())
            assertThat(result).isNotNull()
        }
    }

    @Test
    fun `분산 락을 해제하면 키가 삭제된다`() {
        distributedLockManager.executeWithLock(
            lockKey = lockKey,
            failAcquireLockException = ApiException.from(ErrorCode.E500_INTERNAL_SERVER_ERROR)
        ) {
        }

        val result = redisTemplate.opsForValue().get(lockKey.keyString())
        assertThat(result).isNull()
    }

    companion object {
        val lockKey = DistributedLockKey.of(
            lockType = DistributedLockType.PAY,
            key = "key"
        )
    }
}