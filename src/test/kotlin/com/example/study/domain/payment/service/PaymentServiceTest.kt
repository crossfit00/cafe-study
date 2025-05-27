package com.example.study.domain.payment.service

import com.example.study.common.exception.ApiException
import com.example.study.common.exception.ErrorCode
import com.example.study.domain.item.service.ItemService
import com.example.study.domain.member.entity.Gender
import com.example.study.domain.member.entity.MemberEntity
import com.example.study.domain.member.entity.MemberStatus
import com.example.study.domain.order.entity.OrderEntity
import com.example.study.domain.order.entity.OrderStatus
import com.example.study.domain.order.repository.OrderHistoryRepository
import com.example.study.domain.order.repository.OrderRepository
import com.example.study.domain.order.service.OrderService
import com.example.study.domain.payment.api.PaymentRequest
import com.example.study.domain.payment.api.Payments
import com.example.study.domain.payment.entity.PaymentType
import com.example.study.domain.payment.repository.PaymentRepository
import com.example.study.infra.client.CafePaymentRequestClient
import com.example.study.infra.redis.DistributedLockManager
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.Test

class PaymentServiceTest {

    private val orderService: OrderService = mockk()
    private val itemService: ItemService = mockk()
    private val paymentRepository: PaymentRepository = mockk()
    private val orderRepository: OrderRepository = mockk()
    private val orderHistoryRepository: OrderHistoryRepository = mockk()
    private val distributedLockManager: DistributedLockManager = mockk()
    private val cafePaymentRequestClient: CafePaymentRequestClient = mockk()
    private val paymentService: PaymentService = PaymentService(
        orderService = orderService,
        itemService = itemService,
        paymentRepository = paymentRepository,
        orderHistoryRepository = orderHistoryRepository,
        distributedLockManager = distributedLockManager,
        cafePaymentRequestClient = cafePaymentRequestClient
    )

    private val member = MemberEntity(
        name = "name",
        phoneNumber = "010-1234-5678",
        email = "email@example.com",
        gender = Gender.MALE,
        birth = LocalDate.of(1995, 11, 1),
        status = MemberStatus.SERVICE
    )

    private val paymentRequest = PaymentRequest(
        orderId = 1L,
        payments = listOf(
            Payments(
                type = PaymentType.CARD,
                amount = BigDecimal(100)
            )
        )
    )

    @Test
    fun `주문 상태가 PENDING_PAYMENT가 아니면 E400_INVALID_ORDER_STATUS_FOR_PAYMENT 에러 발생`() {
        val order = OrderEntity(
            totalPrice = BigDecimal(10),
            status = OrderStatus.COMPLETED,
            member = member
        )
        every { orderService.findByIdAndMemberId(any(), any()) } returns order

        val exception = assertThrows(ApiException::class.java) {
            paymentService.save(
                request = paymentRequest,
                memberId = 1L
            )
        }

        assertEquals(ErrorCode.E400_INVALID_ORDER_STATUS_FOR_PAYMENT, exception.errorCode)
    }

    @Test
    fun `주문 totalPrice와 결제 요청 금액이 다르면 E400_INVALID_ORDER_TOTAL_PRICE가 발생한다`() {
        val order = OrderEntity(
            totalPrice = BigDecimal(50),
            status = OrderStatus.COMPLETED,
            member = member
        )
        every { orderService.findByIdAndMemberId(any(), any()) } returns order

        val exception = assertThrows(ApiException::class.java) {
            paymentService.save(
                request = paymentRequest,
                memberId = 1L
            )
        }

        assertEquals(ErrorCode.E400_INVALID_ORDER_STATUS_FOR_PAYMENT, exception.errorCode)
    }
}