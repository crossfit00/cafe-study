package com.example.study.common.code

import org.springframework.http.HttpStatus

/**
 * MemberErrorCode
 *
 * @author JungGyun.Choi
 * @version 1.0.0
 * @since 2025. 05. 27.
 */
enum class MemberErrorCode(
    override val httpStatus: HttpStatus,
    override val minorStatus: String,
    override val defaultMessage: String? = null,
    override val errorType: ErrorType = ErrorType.MEMBER
): ErrorCode {
    // 400
    E400_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "000", "이미 존재하는 이메일 입니다."),
    E400_INVALID_MEMBER_STATUS_FOR_WITHDRAW(HttpStatus.BAD_REQUEST, "001", "멤버 탈퇴시 상태가 올바르지 않습니다."),
}