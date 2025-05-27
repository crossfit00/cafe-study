package com.example.study.domain.order.api

import com.example.study.common.code.CommonErrorCode
import com.example.study.common.exception.ApiException
import com.example.study.common.code.ErrorCode

data class OrderCreateRequest(
    val items: List<OrderItemRequest>
) {
    init {
        if (items.size > MAX_ORDER_ITEM_REQUEST_SIZE) {
            throw ApiException.from(
                errorCode = CommonErrorCode.E400_BAD_REQUEST,
                "주문 생성 요청시 요청 주문 수(${items.size}) 보다 ${MAX_ORDER_ITEM_REQUEST_SIZE}가 많을 수 없습니다."
            )
        }
    }

    companion object {
        private const val MAX_ORDER_ITEM_REQUEST_SIZE = 50
    }
}

data class OrderItemRequest(
    val itemId: Long,
    val quantity: Int
) {
    init {
        if (quantity <= 0) {
            throw ApiException.from(
                errorCode = CommonErrorCode.E400_BAD_REQUEST,
                "주문 생성 요청시 아이템 수가(${quantity}) 0보다 작거나 같을 수 없습니다."
            )
        }
    }
}