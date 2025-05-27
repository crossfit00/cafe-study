package com.example.study.domain.payment.api

import com.example.study.common.exception.ApiResponse
import com.example.study.domain.payment.service.PaymentService
import com.example.study.infra.aop.Auth
import com.example.study.infra.aop.MemberContext
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentApi(
    private val paymentService: PaymentService
) {

    @Auth
    @PostMapping("/payment")
    fun payments(@RequestBody paymentRequest: PaymentRequest): ApiResponse<Nothing> {
        val memberId = MemberContext.getMemberId()
        paymentService.save(paymentRequest, memberId)
        return ApiResponse.success()
    }
}