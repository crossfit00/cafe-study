package com.example.study.infra.client

import com.example.study.common.exception.ApiException
import com.example.study.common.exception.ErrorCode
import org.springframework.stereotype.Component
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

@Component
class CafePaymentRequestClient: PaymentClient {

    override fun makePayment(): String {
        Thread.sleep(abs(Random.nextLong(10)))

        return if ((1..100).random() <= 70) {
            UUID.randomUUID().toString()
        } else {
            throw ApiException.from(
                errorCode = ErrorCode.E500_PAYMENT_REQUEST_SERVER_ERROR,
                resultErrorMessage = "결제 서버에서 에러가 발생하였습니다."
            )
        }
    }

    override fun cancelPayment(paymentUUID: String): String {
        Thread.sleep(abs(Random.nextLong(10)))

        return if ((1..100).random() <= 70) {
            paymentUUID
        } else {
            throw ApiException.from(
                errorCode = ErrorCode.E500_PAYMENT_REQUEST_SERVER_ERROR,
                resultErrorMessage = "결제 서버에서 에러가 발생하였습니다."
            )
        }
    }
}