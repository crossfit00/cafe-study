package com.example.study.domain.payment.repository

import com.example.study.domain.payment.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository : JpaRepository<PaymentEntity, Long> {

    fun findByOrderId(orderId: Long): List<PaymentEntity>
}