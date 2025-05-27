package com.example.study.common.exception

import org.springframework.http.HttpStatus

// FIXME: 도메인 별로 ErrorCode 분리
enum class ErrorCode(
    val httpStatus: HttpStatus,
    val minorStatus: String,
    val defaultMessage: String? = null,
) {
    // ------------------------------ 400 ------------------------------
    E400_BAD_REQUEST(HttpStatus.BAD_REQUEST, "000", "필수 파라미터 값이 없거나 잘못된 값으로 요청을 보낸 경우 발생"),
    E400_INVALID_ORDER_STATUS_FOR_PAYMENT(HttpStatus.BAD_REQUEST, "001", "결제 또는 취소시 주문의 상태가 올바르지 않을 때 발생"),
    E400_INVALID_ORDER_TOTAL_PRICE(HttpStatus.BAD_REQUEST, "002", "결제 요청 금액과 주문 총 금액이 다를 때 발생"),
    E400_NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, "003", "요청 주문의 상품 수보다 존재하는 상품 재고가 부족합니다."),
    E400_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "004", "이미 존재하는 이메일 입니다."),
    E400_INVALID_MEMBER_STATUS_FOR_WITHDRAW(HttpStatus.BAD_REQUEST, "005", "멤버 탈퇴시 상태가 올바르지 않습니다."),

    // ------------------------------ 401 ------------------------------
    E401_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "000", "인증 토큰이 없거나, 유효하지 않은 경우 발생합니다."),

    // ------------------------------ 403 ------------------------------
    E403_FORBIDDEN(HttpStatus.FORBIDDEN, "000", "사용 권한이 없는 경우 발생"),

    // ------------------------------ 404 ------------------------------
    E404_NOT_FOUND(HttpStatus.NOT_FOUND, "000", "요청한 리소스가 존재하지 않는 경우 발생"),
    E404_PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "001", "요청 주문에 맞는 결제가 존재하지 않습니다"),

    // ------------------------------ 405 ------------------------------
    E405_METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "000", "허용하지 않는 HTTP Method를 요청한 경우 발생"),

    // ------------------------------ 500 ------------------------------
    E500_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "000", "서버 내부에서 에러가 발생하는 경우 발생"),
    E500_PAYMENT_REQUEST_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "001", "결제 서버 요청시 에러가 발생하는 경우 발생"),
    E500_PAYMENT_LOCK_ACQUIRE_FAIL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "002", "결제시 락 선점 실패할 경우 발샹"),
    ;

    fun getCode(): String {
        return httpStatus.value().toString() + minorStatus
    }

    override fun toString(): String {
        return getCode()
    }
}
