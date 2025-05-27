package com.example.study.infra.aop

import com.example.study.common.exception.TokenInvalidException
import com.example.study.common.exception.TokenNotFoundException
import com.example.study.domain.member.service.MemberService
import com.example.study.infra.jwt.JwtPayload
import com.example.study.infra.jwt.JwtProvider
import io.jsonwebtoken.JwtException
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class AuthAspect(
    private val httpServletRequest: HttpServletRequest,
    private val jwtProvider: JwtProvider,
    private val memberService: MemberService
) {

    @Around("@annotation(com.example.study.infra.aop.Auth)")
    @Throws(Throwable::class)
    fun accessToken(pjp: ProceedingJoinPoint): Any? {
        return try {
            val accessToken = resolveToken(httpServletRequest)
                ?: throw TokenNotFoundException("HTTP 헤더에 AccessToken이 존재하지 않습니다.")
            val payload = jwtProvider.getPayload(accessToken)
            val member = memberService.findById(payload)
            MemberContext.MEMBER_CONTEXT.set(JwtPayload(member.id))
            pjp.proceed()
        } catch (e: JwtException) {
            throw TokenInvalidException("AccessToken이 만료 됐거나 잘못된 토큰 입니다.")
        }
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION)
        return if (!bearerToken.isNullOrBlank() && bearerToken.startsWith(PREFIX_BEARER)) {
            bearerToken.substring(7)
        } else {
            null
        }
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val PREFIX_BEARER = "Bearer"
    }

}