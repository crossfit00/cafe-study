package com.example.study.common.code

import org.springframework.http.HttpStatus

/**
 * OrderErrorCode
 *
 * @author JungGyun.Choi
 * @version 1.0.0
 * @since 2025. 05. 27.
 */
enum class OrderErrorCode(
    override val httpStatus: HttpStatus,
    override val minorStatus: String,
    override val defaultMessage: String? = null,
    override val errorType: ErrorType = ErrorType.ORDER
): ErrorCode {
    // 400
    E400_NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, "000", "요청 주문의 상품 수보다 존재하는 상품 재고가 부족합니다."),
    E400_INVALID_ORDER_STATUS_FOR_PAYMENT(HttpStatus.BAD_REQUEST, "001", "결제 또는 취소시 주문의 상태가 올바르지 않을 때 발생"),
}