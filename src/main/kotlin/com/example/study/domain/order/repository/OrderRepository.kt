package com.example.study.domain.order.repository

import com.example.study.domain.order.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<OrderEntity, Long> {

    fun findByIdAndMemberId(orderId: Long, memberId: Long): OrderEntity?
}