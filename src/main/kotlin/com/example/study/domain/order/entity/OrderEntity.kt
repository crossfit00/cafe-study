package com.example.study.domain.order.entity

import com.example.study.domain.BaseEntity
import com.example.study.domain.member.entity.MemberEntity
import jakarta.persistence.*
import java.math.BigDecimal

@Table(name = "orders")
@Entity
class OrderEntity(
    @Column(name = "total_price", nullable = false)
    var totalPrice: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: OrderStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberEntity,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "order_id")
    val orderItems: List<OrderItemEntity> = listOf()
): BaseEntity() {
    fun isPayableOrderStatus(): Boolean {
        return status == OrderStatus.PENDING_PAYMENT
    }

    fun isPayCancelableOrderStatus(): Boolean {
        return status == OrderStatus.COMPLETED
    }

    fun cancel() {
        this.status = OrderStatus.CANCELLED
    }
}