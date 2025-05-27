package com.example.study.domain.order.service

import com.example.study.common.code.CommonErrorCode
import com.example.study.common.code.OrderErrorCode
import com.example.study.common.exception.ApiException
import com.example.study.domain.item.entity.ItemEntity
import com.example.study.domain.item.repository.ItemRepository
import com.example.study.domain.item.service.ItemService
import com.example.study.domain.member.entity.Gender
import com.example.study.domain.member.entity.MemberEntity
import com.example.study.domain.member.entity.MemberStatus
import com.example.study.domain.member.service.MemberService
import com.example.study.domain.order.api.OrderCreateRequest
import com.example.study.domain.order.api.OrderItemRequest
import com.example.study.domain.order.entity.OrderEntity
import com.example.study.domain.order.entity.OrderStatus
import com.example.study.domain.order.repository.OrderRepository
import com.example.study.domain.payment.repository.PaymentHistoryRepository
import com.example.study.domain.payment.repository.PaymentRepository
import com.example.study.infra.client.PaymentClient
import com.example.study.infra.redis.DistributedLockManager
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.any
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.Test

class OrderServiceTest {

    private val itemService: ItemService = mockk()
    private val memberService: MemberService = mockk()
    private val orderRepository: OrderRepository = mockk()
    private val paymentRepository: PaymentRepository = mockk()
    private val itemRepository: ItemRepository = mockk()
    private val paymentHistoryRepository: PaymentHistoryRepository = mockk()
    private val distributedLockManager: DistributedLockManager = mockk()
    private val cafePaymentRequestClient: PaymentClient = mockk()

    private val orderService: OrderService = OrderService(
        itemService = itemService,
        memberService = memberService,
        orderRepository = orderRepository,
        paymentRepository = paymentRepository,
        itemRepository = itemRepository,
        paymentHistoryRepository = paymentHistoryRepository,
        distributedLockManager = distributedLockManager,
        cafePaymentRequestClient = cafePaymentRequestClient
    )

    @Nested
    inner class OrderCreateTest {
        @Test
        fun `주문 생성시 존재하지 않는 상품 아이디는 E404_NOT_FOUND 발생한다`() {
            val member = MemberEntity(
                name = "name",
                phoneNumber = "010-1234-5678",
                email = "email@example.com",
                gender = Gender.MALE,
                birth = LocalDate.of(1995, 11, 1),
                status = MemberStatus.SERVICE
            )

            every { memberService.findById(any()) } returns member
            every { itemService.findByIdIn(any()) } returns emptyList()

            val exception = assertThrows(ApiException::class.java) {
                orderService.create(
                    request = OrderCreateRequest(
                        items = listOf(
                            OrderItemRequest(
                                itemId = 1L,
                                quantity = 51
                            )
                        )
                    ),
                    memberId = member.id
                )
            }

            assertEquals(CommonErrorCode.E404_NOT_FOUND, exception.errorCode)
        }

        @Test
        fun `주문 요청 상품 수보다 상품 재고가 부족한 경우 E400_NOT_ENOUGH_STOCK 발생한다`() {
            val member = MemberEntity(
                name = "name",
                phoneNumber = "010-1234-5678",
                email = "email@example.com",
                gender = Gender.MALE,
                birth = LocalDate.of(1995, 11, 1),
                status = MemberStatus.SERVICE
            )

            val item = ItemEntity(
                name = "아이템",
                price = BigDecimal(100),
                stock = 10
            )

            item.id = 1L

            every { memberService.findById(any()) } returns member
            every { itemService.findByIdIn(any()) } returns listOf(item)

            val exception = assertThrows(ApiException::class.java) {
                orderService.create(
                    request = OrderCreateRequest(
                        items = listOf(
                            OrderItemRequest(
                                itemId = 1L,
                                quantity = 51
                            )
                        )
                    ),
                    memberId = member.id
                )
            }

            assertEquals(OrderErrorCode.E400_NOT_ENOUGH_STOCK, exception.errorCode)
        }
    }

    @Test
    fun `멤버가 주문한 주문 조회가 아니라면 E404_NOT_FOUND 에러 발생한다`() {
        every { orderRepository.findByIdAndMemberId(any(), any()) } returns null

        val exception = assertThrows(ApiException::class.java) {
            orderService.findByIdAndMemberId(any(), any())
        }

        assertEquals(CommonErrorCode.E403_FORBIDDEN, exception.errorCode)
    }

    @Nested
    inner class OrderCancelTest {
        @Test
        fun `주문 취소 상태가 아니면 E400_INVALID_ORDER_STATUS_FOR_PAYMENT 발생한다`() {
            val member = MemberEntity(
                name = "name",
                phoneNumber = "010-1234-5678",
                email = "email@example.com",
                gender = Gender.MALE,
                birth = LocalDate.of(1995, 11, 1),
                status = MemberStatus.SERVICE
            )

            val order = OrderEntity(
                totalPrice = BigDecimal(1000),
                status = OrderStatus.PENDING_PAYMENT,
                member = member,
                orderItems = listOf()
            )
            every { orderRepository.findByIdAndMemberId(any(), any()) } returns order

            val exception = assertThrows(ApiException::class.java) {
                orderService.cancel(any(), any())
            }

            assertEquals(OrderErrorCode.E400_INVALID_ORDER_STATUS_FOR_PAYMENT, exception.errorCode)
        }

        @Test
        fun `주문의 결제 내역이 존재하지 않으면 E404_NOT_FOUND 발생한다`() {
            val member = MemberEntity(
                name = "name",
                phoneNumber = "010-1234-5678",
                email = "email@example.com",
                gender = Gender.MALE,
                birth = LocalDate.of(1995, 11, 1),
                status = MemberStatus.SERVICE
            )

            val order = OrderEntity(
                totalPrice = BigDecimal(1000),
                status = OrderStatus.COMPLETED,
                member = member,
                orderItems = listOf()
            )

            every { orderRepository.findByIdAndMemberId(any(), any()) } returns order
            every { paymentRepository.findByOrderId(any()) } returns listOf()

            val exception = assertThrows(ApiException::class.java) {
                orderService.cancel(any(), any())
            }

            assertEquals(CommonErrorCode.E404_NOT_FOUND, exception.errorCode)
        }
    }
}