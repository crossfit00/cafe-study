package com.example.study.domain.item.repository

import com.example.study.domain.item.entity.ItemEntity
import org.springframework.data.jpa.repository.JpaRepository


interface ItemRepository : JpaRepository<ItemEntity, Long> {

    fun findItemByIdIn(itemIds: Collection<Long>): List<ItemEntity>
}