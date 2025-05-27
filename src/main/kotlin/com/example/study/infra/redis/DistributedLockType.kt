package com.example.study.infra.redis

enum class DistributedLockType(
    val prefix: String,
) {
    MEMBER("m"),
    PAY("p"),
    ;
}