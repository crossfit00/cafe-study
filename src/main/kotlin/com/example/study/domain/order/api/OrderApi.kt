package com.example.study.domain.order.api

import com.example.study.common.exception.ApiResponse
import com.example.study.domain.order.service.OrderService
import com.example.study.infra.aop.Auth
import com.example.study.infra.aop.MemberContext
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderApi(
    private val orderService: OrderService,
) {

    @Auth
    @PostMapping("/order")
    fun createOrder(@RequestBody request: OrderCreateRequest): ApiResponse<Nothing> {
        val memberId = MemberContext.getMemberId()
        orderService.create(request, memberId)
        return ApiResponse.success()
    }

    @Auth
    @DeleteMapping("/order/{orderId}/cancel")
    fun cancelOrder(@PathVariable orderId: Long): ApiResponse<Nothing> {
        val memberId = MemberContext.getMemberId()
        orderService.cancel(orderId, memberId)
        return ApiResponse.success()
    }
}