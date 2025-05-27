package com.example.study.domain.payment.entity

import com.example.study.domain.BaseEntity
import com.example.study.domain.order.entity.OrderEntity
import jakarta.persistence.*
import java.math.BigDecimal

@Table(name = "payment")
@Entity
class PaymentEntity(
    @Column(name = "amount", nullable = false)
    val amount: BigDecimal,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: OrderEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: PaymentType,

    @Column(name = "payment_uuid", nullable = false)
    val uuid: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PaymentStatus,
): BaseEntity() {

    fun cancel() {
        this.status = PaymentStatus.CANCELED
    }
}