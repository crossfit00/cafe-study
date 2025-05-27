package com.example.study.domain.item.service

import com.example.study.common.exception.ApiException
import com.example.study.common.exception.ErrorCode
import com.example.study.domain.item.entity.ItemEntity
import com.example.study.domain.item.repository.ItemRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository
) {

    fun findByIdIn(itemIds: Collection<Long>): List<ItemEntity> {
        return itemRepository.findItemByIdIn(itemIds)
    }

    fun findById(itemId: Long): ItemEntity {
        return itemRepository.findByIdOrNull(itemId) ?: throw ApiException.from(
            errorCode = ErrorCode.E404_NOT_FOUND,
            resultErrorMessage = "itemId($itemId)에 해당하는 리소스가 존재하지 않습니다."
        )
    }
}