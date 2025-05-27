package com.example.study.domain.payment.repository

import com.example.study.domain.payment.entity.PaymentHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentHistoryRepository : JpaRepository<PaymentHistoryEntity, Long>