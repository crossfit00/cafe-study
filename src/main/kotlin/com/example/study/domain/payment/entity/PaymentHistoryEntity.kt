package com.example.study.domain.payment.entity

import com.example.study.domain.BaseEntity
import jakarta.persistence.*

@Table(name = "payment_history")
@Entity
class PaymentHistoryEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    val payment: PaymentEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: PaymentStatus
): BaseEntity()