package com.example.study.infra.config

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.embedded.RedisServer

@Configuration
class EmbeddedRedisConfig {

    private lateinit var redisServer: RedisServer

    @PostConstruct
    fun startRedis() {
        redisServer = RedisServer(6380)
        redisServer.start()
    }

    @PreDestroy
    fun stopRedis() = redisServer.stop()

    @Bean
    fun redisServer(): RedisServer = redisServer
}