package com.example.study.domain.order.entity

enum class OrderStatus(
    private val description: String
) {
    PENDING_PAYMENT("결제 대기"),
    COMPLETED("주문 완료"),
    CANCELLED("주문 취소")
}