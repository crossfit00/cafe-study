package com.example.study.domain.order.service

import com.example.study.common.exception.ApiException
import com.example.study.domain.item.entity.ItemEntity
import com.example.study.domain.item.repository.ItemRepository
import com.example.study.domain.member.entity.Gender
import com.example.study.domain.member.entity.MemberEntity
import com.example.study.domain.member.entity.MemberStatus
import com.example.study.domain.member.repository.MemberRepository
import com.example.study.domain.order.entity.OrderEntity
import com.example.study.domain.order.entity.OrderItemEntity
import com.example.study.domain.order.entity.OrderStatus
import com.example.study.domain.order.repository.OrderRepository
import com.example.study.domain.payment.api.PaymentRequest
import com.example.study.domain.payment.api.Payments
import com.example.study.domain.payment.entity.PaymentType
import com.example.study.domain.payment.service.PaymentService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import redis.embedded.RedisServer
import java.math.BigDecimal
import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test

@SpringBootTest
class OrderServiceIntegrationTest {

    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var paymentService: PaymentService

    @Autowired
    private lateinit var redisServer: RedisServer

    @Autowired
    private lateinit var itemRepository: ItemRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @BeforeEach
    fun init() {
        val member = memberRepository.save(
            MemberEntity(
                name = "name",
                phoneNumber = "010-1234-5678",
                email = "email@example.com",
                gender = Gender.MALE,
                birth = LocalDate.of(1995, 11, 1),
                status = MemberStatus.SERVICE
            )
        )

        val item = itemRepository.save(
            ItemEntity(
                name = "상품",
                price = BigDecimal(10000),
                stock = 5
            )
        )

        val orderItem = OrderItemEntity(
            quantity = 5,
            unitPrice = BigDecimal(10000),
            item = item
        )

        orderRepository.save(
            OrderEntity(
                totalPrice = BigDecimal(50000),
                status = OrderStatus.PENDING_PAYMENT,
                member = member,
                orderItems = listOf(orderItem)
            )
        )
    }

    @Test
    fun `동일한 orderId 에 대해 동시에 결제를 시도하면 하나만 성공해야 한다`() {
        val numberOfThreads = 10
        val executor = Executors.newFixedThreadPool(numberOfThreads)
        val latch = CountDownLatch(numberOfThreads)
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        repeat(numberOfThreads) {
            executor.submit {
                try {
                    paymentService.save(
                        request = PaymentRequest(
                            orderId = 1L,
                            payments = listOf(
                                Payments(
                                    type = PaymentType.CARD,
                                    amount = BigDecimal(50000)
                                )
                            )
                        ),
                        memberId = 2L
                    )
                    successCount.incrementAndGet()
                } catch (e: ApiException) {
                    failureCount.incrementAndGet()
                } catch (t: Throwable) {
                    failureCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await(10, TimeUnit.SECONDS)
        executor.shutdown()

        assertEquals(1, successCount.get())
        assertEquals(9, failureCount.get())
    }
}