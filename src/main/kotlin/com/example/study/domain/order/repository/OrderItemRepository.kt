package com.example.study.domain.order.repository

import com.example.study.domain.order.entity.OrderItemEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItemEntity, Long>