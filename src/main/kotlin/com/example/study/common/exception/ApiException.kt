package com.example.study.common.exception

import com.example.study.common.code.ErrorCode

class ApiException private constructor(
    val errorCode: ErrorCode,
    val resultErrorMessage: String?,
    override val cause: Throwable?,
) : RuntimeException(
    cause
) {
    companion object {
        fun from(
            errorCode: ErrorCode,
            resultErrorMessage: String? = null,
            cause: Throwable? = null
        ): ApiException {
            return ApiException(
                errorCode,
                resultErrorMessage,
                cause
            )
        }
    }
}