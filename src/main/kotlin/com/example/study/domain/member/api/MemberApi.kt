package com.example.study.domain.member.api

import com.example.study.common.code.CommonErrorCode
import com.example.study.common.exception.ApiException
import com.example.study.common.exception.ApiResponse
import com.example.study.common.code.ErrorCode
import com.example.study.domain.member.service.MemberService
import com.example.study.infra.aop.Auth
import com.example.study.infra.aop.MemberContext
import com.example.study.infra.jwt.JwtProvider
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
class MemberApi(
    private val memberService: MemberService,
    private val jwtProvider: JwtProvider
) {

    @PostMapping("/member")
    fun register(@Valid @RequestBody request: MemberRequest): ApiResponse<MemberResponse> {
        val memberId = memberService.register(request)
        return ApiResponse.success(MemberResponse(jwtProvider.createAccessToken(memberId)))
    }

    @Auth
    @DeleteMapping("/member/{memberId}/withdraw")
    fun withdraw(@PathVariable memberId: Long): ApiResponse<Nothing> {
        val memberIdByToken = MemberContext.getMemberId()
        validateRequestMemberIdAndTokenMemberId(memberId, memberIdByToken)
        memberService.withdraw(memberId)
        return ApiResponse.success()
    }

    private fun validateRequestMemberIdAndTokenMemberId(
        memberIdByRequest: Long,
        memberIdByToken: Long
    ) {
        if (memberIdByRequest != memberIdByToken) {
            throw ApiException.from(
                errorCode = CommonErrorCode.E403_FORBIDDEN,
                "memberIdByToken(${memberIdByToken})는 memberIdByRequest(${memberIdByRequest}) 멤버를 탈퇴할 권한이 없습니다."
            )
        }
    }
}