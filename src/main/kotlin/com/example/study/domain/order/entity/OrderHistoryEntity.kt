package com.example.study.domain.order.entity

import com.example.study.domain.BaseEntity
import jakarta.persistence.*

@Table(name = "orders_history")
@Entity
class OrderHistoryEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: OrderEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: OrderStatus
): BaseEntity()