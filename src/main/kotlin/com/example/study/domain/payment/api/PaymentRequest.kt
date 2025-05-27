package com.example.study.domain.payment.api

import com.example.study.common.code.CommonErrorCode
import com.example.study.common.exception.ApiException
import com.example.study.common.code.ErrorCode
import com.example.study.domain.payment.entity.PaymentType
import java.math.BigDecimal

data class PaymentRequest(
    val orderId: Long,
    val payments: List<Payments>
) {
    fun totalAmount(): BigDecimal = payments.sumOf { it.amount }
}

data class Payments(
    val type: PaymentType,
    val amount: BigDecimal
) {
    init {
        if (amount <= BigDecimal(0)) {
            throw ApiException.from(
                errorCode = CommonErrorCode.E400_BAD_REQUEST,
                "결제 요청시 결제 금액(${amount})이 0보다 작거나 같을 수 없습니다."
            )
        }
    }
}