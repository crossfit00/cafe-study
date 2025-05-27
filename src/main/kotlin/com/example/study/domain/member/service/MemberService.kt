package com.example.study.domain.member.service

import com.example.study.common.exception.ApiException
import com.example.study.common.exception.ErrorCode
import com.example.study.domain.member.api.MemberRequest
import com.example.study.domain.member.entity.MemberEntity
import com.example.study.domain.member.entity.MemberHistoryEntity
import com.example.study.domain.member.repository.MemberHistoryRepository
import com.example.study.domain.member.repository.MemberRepository
import com.example.study.event.MemberHistoryEvent
import com.example.study.infra.redis.DistributedLockKey
import com.example.study.infra.redis.DistributedLockManager
import com.example.study.infra.redis.DistributedLockType
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberHistoryRepository: MemberHistoryRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val distributedLockManager: DistributedLockManager
) {

    @Transactional
    fun register(request: MemberRequest): Long {
        val lockKey = DistributedLockKey.of(
            lockType = DistributedLockType.MEMBER,
            key = "member:${request.email}"
        )

        return distributedLockManager.executeWithLock(
            lockKey = lockKey,
            failAcquireLockException = ApiException.from(
                errorCode = ErrorCode.E500_PAYMENT_LOCK_ACQUIRE_FAIL_ERROR,
                resultErrorMessage = "결제 진행 중에 분산락 선점에 실패 했습니다. (lockKey = ${lockKey})"
            )
        ) {
            val email = request.email
            if (memberRepository.existsByEmail(email)) {
                throw ApiException.from(ErrorCode.E400_EXIST_EMAIL, "요청 이메일($email)는 이미 존재합니다.")
            }

            return@executeWithLock memberRepository.save(request.toEntity()).id
        }
    }

    @Transactional
    fun withdraw(memberId: Long) {
        val member = findById(memberId)
        member.updateWithDrawMember()
        memberHistoryRepository.save(MemberHistoryEntity.toEntity(member))

        // 탈퇴 이력 이벤트 전송
        applicationEventPublisher.publishEvent(MemberHistoryEvent.from(member.id))
    }

    fun findById(memberId: Long): MemberEntity {
        return memberRepository.findByIdOrNull(memberId)
            ?: throw ApiException.from(errorCode = ErrorCode.E404_NOT_FOUND, "memberId($memberId)의 Member가 존재하지 않습니다.")
    }
}