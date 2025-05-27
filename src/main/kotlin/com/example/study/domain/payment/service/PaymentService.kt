package com.example.study.domain.payment.service

import com.example.study.common.exception.ApiException
import com.example.study.common.code.ErrorCode
import com.example.study.common.code.OrderErrorCode
import com.example.study.common.code.PayErrorCode
import com.example.study.domain.item.service.ItemService
import com.example.study.domain.order.entity.OrderHistoryEntity
import com.example.study.domain.order.entity.OrderStatus
import com.example.study.domain.order.repository.OrderHistoryRepository
import com.example.study.domain.order.service.OrderService
import com.example.study.domain.payment.api.PaymentRequest
import com.example.study.domain.payment.entity.PaymentEntity
import com.example.study.domain.payment.entity.PaymentStatus
import com.example.study.domain.payment.repository.PaymentRepository
import com.example.study.infra.client.PaymentClient
import com.example.study.infra.redis.DistributedLockKey
import com.example.study.infra.redis.DistributedLockManager
import com.example.study.infra.redis.DistributedLockType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val orderService: OrderService,
    private val itemService: ItemService,
    private val paymentRepository: PaymentRepository,
    private val orderHistoryRepository: OrderHistoryRepository,
    private val distributedLockManager: DistributedLockManager,
    private val cafePaymentRequestClient: PaymentClient
) {
    @Transactional
    fun save(request: PaymentRequest, memberId: Long) {
        val order = orderService.findByIdAndMemberId(request.orderId, memberId)
        if (!order.isPayableOrderStatus()) {
            throw ApiException.from(
                errorCode = OrderErrorCode.E400_INVALID_ORDER_STATUS_FOR_PAYMENT,
                resultErrorMessage = "orderId(${order.id})의 status(${order.status})가 주문 대기 상태가 아니어서 결제를 진행할 수 없습니다."
            )
        }

        if (order.totalPrice != request.totalAmount()) {
            throw ApiException.from(
                errorCode = PayErrorCode.E400_INVALID_ORDER_TOTAL_PRICE,
                resultErrorMessage = "주문 총 금액과(${order.totalPrice})의 결제 요청 금액(${request.totalAmount()})이 달라서 결제를 진행할 수 없습니다."
            )
        }

        val orderItemKeys = order.orderItems.map { it.item.id }
        val lockKey = DistributedLockKey.of(
            lockType = DistributedLockType.PAY,
            key = "p:${order.id}:${orderItemKeys.joinToString(",")}"
        )

        distributedLockManager.executeWithLock(
            lockKey = lockKey,
            failAcquireLockException = ApiException.from(
                errorCode = PayErrorCode.E500_PAYMENT_LOCK_ACQUIRE_FAIL_ERROR,
                resultErrorMessage = "결제 진행 중에 분산락 선점에 실패 했습니다. (lockKey = ${lockKey})"
            )
        ) {
            // TODO: 현재는 스펙상 JPA 사용하지만 MyBatis or JDBC Template Bulk Update 고려해보면 좋을 듯
            order.orderItems.forEach { orderItem ->
                val item = itemService.findById(orderItem.item.id)
                item.decreaseStock(orderItem.quantity)
            }

            order.status = OrderStatus.COMPLETED
            orderService.save(order)
            val orderHistory = OrderHistoryEntity(
                order = order,
                status = order.status
            )

            orderHistoryRepository.save(orderHistory)

            val paymentUUID = cafePaymentRequestClient.makePayment()
            val paymentEntities = request.payments.map { payment ->
                PaymentEntity(
                    amount = payment.amount,
                    order = order,
                    type = payment.type,
                    status = PaymentStatus.PAID,
                    uuid = paymentUUID
                )
            }

            // TODO: 현재는 스펙상 JPA 사용하여 개발하지만, JPA 특성상 saveAll 사용하면 성능이 좋지 않기 때문에 MyBatis or JDBC Template 통해서 Bulk Insert 변경해보면 좋을 듯
            paymentRepository.saveAll(paymentEntities)
        }
    }
}