package com.example.study.infra.aop

import com.example.study.infra.jwt.JwtPayload

object MemberContext {
    val MEMBER_CONTEXT: ThreadLocal<JwtPayload> = ThreadLocal()

    fun getMemberId(): Long {
        return MEMBER_CONTEXT.get()?.memberId ?: throw IllegalArgumentException()
    }
}