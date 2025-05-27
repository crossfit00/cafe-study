package com.example.study.domain.order.service

import com.example.study.common.exception.ApiException
import com.example.study.common.exception.ErrorCode
import com.example.study.domain.item.entity.ItemEntity
import com.example.study.domain.item.service.ItemService
import com.example.study.domain.member.service.MemberService
import com.example.study.domain.order.api.OrderCreateRequest
import com.example.study.domain.order.entity.OrderEntity
import com.example.study.domain.order.entity.OrderItemEntity
import com.example.study.domain.order.entity.OrderStatus
import com.example.study.domain.order.repository.OrderRepository
import com.example.study.domain.payment.entity.PaymentHistoryEntity
import com.example.study.domain.payment.entity.PaymentStatus
import com.example.study.domain.payment.repository.PaymentHistoryRepository
import com.example.study.domain.payment.repository.PaymentRepository
import com.example.study.infra.client.CafePaymentRequestClient
import com.example.study.infra.client.PaymentClient
import com.example.study.infra.redis.DistributedLockKey
import com.example.study.infra.redis.DistributedLockManager
import com.example.study.infra.redis.DistributedLockType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class OrderService(
    private val itemService: ItemService,
    private val memberService: MemberService,
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val paymentHistoryRepository: PaymentHistoryRepository,
    private val distributedLockManager: DistributedLockManager,
    private val cafePaymentRequestClient: PaymentClient
) {

    @Transactional
    fun create(request: OrderCreateRequest, memberId: Long): OrderEntity {
        val member = memberService.findById(memberId)
        val items = request.items
        val itemIds = items.map { it.itemId }
        val itemMaps: Map<Long, ItemEntity> = itemService.findByIdIn(itemIds).associateBy { it.id }

        val orderItemEntities = items.map { orderItem ->
            val item = itemMaps[orderItem.itemId] ?: throw ApiException.from(
                errorCode = ErrorCode.E404_NOT_FOUND,
                "ItemId(${orderItem.itemId})에 해당하는 상품이 존재하지 않습니다."
            )

            item.validateStock(orderItem.quantity)
            OrderItemEntity(
                quantity = orderItem.quantity,
                unitPrice = item.price,
                item = item,
            )
        }

        val orderEntity = OrderEntity(
            status = OrderStatus.PENDING_PAYMENT,
            member = member,
            totalPrice = orderItemEntities.sumOf { it.unitPrice * BigDecimal(it.quantity) },
            orderItems = orderItemEntities
        )

        return orderRepository.save(orderEntity)
    }

    @Transactional
    fun cancelPayments(orderId: Long, memberId: Long) {
        val order = findByIdAndMemberId(orderId, memberId)
        if (!order.isPayCancelableOrderStatus()) {
            throw ApiException.from(
                errorCode = ErrorCode.E400_INVALID_ORDER_STATUS_FOR_PAYMENT,
                resultErrorMessage = "memberId(${memberId}), orderId(${order.id})의 status(${order.status})가 주문 완료 상태가 아니어서 주문 취소를 진행할 수 없습니다."
            )
        }

        val paymentsByOrder = paymentRepository.findByOrderId(order.id)
        val paymentUUID = paymentsByOrder.first().uuid
        cafePaymentRequestClient.cancelPayment(paymentUUID)

        val lockKey = DistributedLockKey.of(
            lockType = DistributedLockType.PAY,
            key = "order:${order.id}:pay:$paymentUUID}"
        )

        distributedLockManager.executeWithLock(
            lockKey = lockKey,
            failAcquireLockException = ApiException.from(
                errorCode = ErrorCode.E500_PAYMENT_LOCK_ACQUIRE_FAIL_ERROR,
                resultErrorMessage = "주문 취소 진행 중에 분산락 선점에 실패 했습니다. (lockKey = ${lockKey})"
            )
        ) {
            order.cancel()
            orderRepository.save(order)

            val paymentHistories = paymentsByOrder.map {
                payment -> payment.cancel()
                PaymentHistoryEntity(
                    payment = payment,
                    status = payment.status
                )
            }

            // TODO: 현재는 스펙상 JPA 사용하여 개발하지만, JPA 특성상 saveAll 사용하면 성능이 좋지 않기 때문에 MyBatis or JDBC Template 통해서 Bulk Insert 변경 필요함
            paymentHistoryRepository.saveAll(paymentHistories)
        }
    }

    @Transactional
    fun save(orderEntity: OrderEntity) {
        orderRepository.save(orderEntity)
    }

    fun findByIdAndMemberId(orderId: Long, memberId: Long): OrderEntity {
        return orderRepository.findByIdAndMemberId(orderId, memberId) ?: throw ApiException.from(
            errorCode = ErrorCode.E403_FORBIDDEN,
            resultErrorMessage = "memberId($memberId)가 주문한 orderId($orderId)에 해당하는 주문이 존재하지 않습니다."
        )
    }
}