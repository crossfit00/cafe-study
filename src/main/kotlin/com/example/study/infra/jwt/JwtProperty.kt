package com.example.study.infra.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperty(
    val secretKey: String,
    val validTime: Long
)