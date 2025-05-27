package com.example.study.domain.order.repository

import com.example.study.domain.order.entity.OrderHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OrderHistoryRepository : JpaRepository<OrderHistoryEntity, Long>