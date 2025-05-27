package com.example.study

import com.example.study.infra.jwt.JwtProperty
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(JwtProperty::class)
@SpringBootApplication
class SpringStudyApplication

fun main(args: Array<String>) {
    runApplication<SpringStudyApplication>(*args)
}
