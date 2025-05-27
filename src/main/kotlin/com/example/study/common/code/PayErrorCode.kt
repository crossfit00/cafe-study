package com.example.study.common.code

import org.springframework.http.HttpStatus

/**
 * PayErrorCode
 *
 * @author JungGyun.Choi
 * @version 1.0.0
 * @since 2025. 05. 27.
 */
enum class PayErrorCode(
    override val httpStatus: HttpStatus,
    override val minorStatus: String,
    override val defaultMessage: String? = null,
    override val errorType: ErrorType = ErrorType.PAY
): ErrorCode {
    // 400
    E400_INVALID_ORDER_TOTAL_PRICE(HttpStatus.BAD_REQUEST, "000", "결제 요청 금액과 주문 총 금액이 다를 때 발생"),

    // 500
    E500_PAYMENT_REQUEST_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "000", "결제 서버 요청시 에러가 발생하는 경우 발생"),
    E500_PAYMENT_LOCK_ACQUIRE_FAIL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "001", "결제시 락 선점 실패할 경우 발샹"),
}