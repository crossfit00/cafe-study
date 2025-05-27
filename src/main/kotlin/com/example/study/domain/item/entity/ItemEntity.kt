package com.example.study.domain.item.entity

import com.example.study.common.exception.ApiException
import com.example.study.common.exception.ErrorCode
import com.example.study.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.math.BigDecimal

@Table(name = "item")
@Entity
class ItemEntity(
    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "price", nullable = false)
    val price: BigDecimal,

    @Column(name = "stock", nullable = false)
    var stock: Long
): BaseEntity() {
    fun decreaseStock(quantity: Int) {
        validateStock(quantity)
        this.stock -= quantity
    }

    fun validateStock(quantity: Int) {
        if (this.stock < quantity) {
            throw ApiException.from(
                errorCode = ErrorCode.E400_NOT_ENOUGH_STOCK,
                "상품(${this.id})의 재고가 부족합니다. (요청 수량: $quantity, 남은 수량: ${this.stock})"
            )
        }
    }
}