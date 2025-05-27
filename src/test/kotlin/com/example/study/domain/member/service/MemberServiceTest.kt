package com.example.study.domain.member.service

import com.example.study.common.exception.ApiException
import com.example.study.common.exception.ErrorCode
import com.example.study.domain.member.api.MemberRequest
import com.example.study.domain.member.entity.Gender
import com.example.study.domain.member.entity.MemberEntity
import com.example.study.domain.member.entity.MemberHistoryEntity
import com.example.study.domain.member.entity.MemberStatus
import com.example.study.domain.member.repository.MemberHistoryRepository
import com.example.study.domain.member.repository.MemberRepository
import com.example.study.event.MemberHistoryEvent
import com.example.study.infra.redis.DistributedLockManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate
import java.util.*

class MemberServiceTest {

    private val memberRepository: MemberRepository = mockk()
    private val memberHistoryRepository: MemberHistoryRepository = mockk()
    private val applicationEventPublisher: ApplicationEventPublisher = mockk()
    private val distributedLockManager: DistributedLockManager = mockk()
    private val memberService: MemberService = MemberService(
        memberRepository = memberRepository,
        applicationEventPublisher = applicationEventPublisher,
        distributedLockManager = distributedLockManager,
        memberHistoryRepository = memberHistoryRepository
    )

    @Nested
    inner class CreateMemberTest {
        @Test
        fun `이미 존재하는 이메일이면 E404_NOT_FOUND 예외를 발생시킨다`() {
            val request = MemberRequest(
                name = "name",
                email = "wjdrbs966@naver.com",
                gender = Gender.MALE,
                phoneNumber = "010-1234-5678",
                birth = LocalDate.of(1994, 11, 5)
            )

            every {
                distributedLockManager.executeWithLock<Long>(
                    any(),
                    any(),
                    any()
                )
            } answers {
                val block = args[2] as () -> Long
                block()
            }

            every { memberRepository.existsByEmail(request.email) }.returns(true)

            val exception = assertThrows(ApiException::class.java) {
                memberService.register(request)
            }

            assertEquals(ErrorCode.E400_EXIST_EMAIL, exception.errorCode)
        }

        @Test
        fun `존재하지 않는 멤버이면 E404_NOT_FOUND 예외를 발생시킨다`() {
            every { memberRepository.findByIdOrNull(1L) } returns null

            val exception = assertThrows(ApiException::class.java) {
                memberService.findById(1L)
            }

            assertEquals(ErrorCode.E404_NOT_FOUND, exception.errorCode)
        }

        @Test
        fun `회원 가입이 정상적으로 진행된다`() {
            val request = MemberRequest(
                name = "name",
                email = "wjdrbs966@naver.com",
                gender = Gender.MALE,
                phoneNumber = "010-1234-5678",
                birth = LocalDate.of(1994, 11, 5)
            )

            every {
                distributedLockManager.executeWithLock<Long>(
                    any(),
                    any(),
                    any()
                )
            } answers {
                val block = args[2] as () -> Long
                block()
            }

            every { memberRepository.existsByEmail(any()) } returns false
            every { memberRepository.save(any()) } returns request.toEntity() // return 값 반환하기 때문에 Mocking 필요함

            memberService.register(request)
            verify(exactly = 1) { memberRepository.save(any()) }
        }
    }

    @Nested
    inner class WithDrawMemberTest {
        @Test
        fun `회원 탈퇴가 정상적으로 진행된다`() {
            val memberEntity = mockk<MemberEntity>(relaxed = true)
            memberEntity.id = 1L

            val memberHistoryEntity = MemberHistoryEntity(
                member = memberEntity,
                status = MemberStatus.WITHDRAWN
            )

            every { memberRepository.findByIdOrNull(memberEntity.id) } returns memberEntity
            every { memberHistoryRepository.save(any()) } returns memberHistoryEntity
            every { applicationEventPublisher.publishEvent(MemberHistoryEvent(memberId = memberEntity.id)) } returns Unit

            memberService.withdraw(memberEntity.id)

            verify(exactly = 1) { memberEntity.updateWithDrawMember() }
            verify(exactly = 1) { memberHistoryRepository.save(any()) }
        }

        @Test
        fun `회원 탈퇴시 멤버 Status가 SERVICE가 아니면 E400_INVALID_MEMBER_STATUS_FOR_WITHDRAW 발생한다`() {
            val memberId = 1L
            val memberEntity = MemberEntity(
                name = "name",
                email = "wjdrbs966@naver.com",
                gender = Gender.MALE,
                phoneNumber = "010-1234-5678",
                birth = LocalDate.of(1994, 11, 5),
                status = MemberStatus.WITHDRAWN
            )

            val memberHistoryEntity = mockk<MemberHistoryEntity>()

            every { memberRepository.findById(memberId) } returns Optional.of(memberEntity)
            every { memberHistoryRepository.save(any()) } returns memberHistoryEntity

            val exception = assertThrows(ApiException::class.java) {
                memberService.withdraw(memberId)
            }

            assertEquals(ErrorCode.E400_INVALID_MEMBER_STATUS_FOR_WITHDRAW, exception.errorCode)
        }
    }
}