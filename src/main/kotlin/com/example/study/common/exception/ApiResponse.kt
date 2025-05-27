package com.example.study.common.exception

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiResponse<T>(
    val status: String,
    val result: T?,
    val error: ErrorResponse?,
) {
    companion object {
        fun <T> success(result: T? = null) = ApiResponse(SUCCESS, result, null)

        fun <T> fail(
            errorCode: ErrorCode,
            detailMessage: String? = null
        ): ApiResponse<T> {
            return ApiResponse(FAIL, null, ErrorResponse(errorCode, detailMessage))
        }

        private const val SUCCESS = "success"
        private const val FAIL = "fail"
    }
}