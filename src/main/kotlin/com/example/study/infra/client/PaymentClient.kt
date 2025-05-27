package com.example.study.infra.client

interface PaymentClient {

    fun makePayment(): String

    fun cancelPayment(paymentUUID: String): String
}