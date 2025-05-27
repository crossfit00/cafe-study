package com.example.study.domain.order.entity

import com.example.study.domain.BaseEntity
import com.example.study.domain.item.entity.ItemEntity
import jakarta.persistence.*
import java.math.BigDecimal

@Table(name = "orders_item")
@Entity
class OrderItemEntity(
    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "unit_price", nullable = false)
    val unitPrice: BigDecimal,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    val item: ItemEntity,
): BaseEntity()