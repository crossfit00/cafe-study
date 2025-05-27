package com.example.study.common.exception

import com.example.study.common.code.ErrorCode
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang3.StringUtils

class ErrorResponse(
    errorCode: ErrorCode,
    detailMessage: String?,
) {
    @JsonIgnore
    private val errorCode: ErrorCode = errorCode

    @JsonIgnore
    private val detailMessage: String? = detailMessage

    @JsonProperty("code")
    fun getCode(): String {
        return errorCode.code
    }

    @JsonProperty("detailMessage")
    fun getDetailMessage(): String? {
        return StringUtils.defaultIfEmpty(detailMessage, errorCode.defaultMessage)
    }
}