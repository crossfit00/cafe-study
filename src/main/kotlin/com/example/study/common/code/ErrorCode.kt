package com.example.study.common.code

import org.springframework.http.HttpStatus

interface ErrorCode {
    val httpStatus: HttpStatus
    val minorStatus: String
    val defaultMessage: String?
    val errorType: ErrorType

    val code: String
        get() = "${httpStatus.value()}${errorType.typeCode}$minorStatus"
}
