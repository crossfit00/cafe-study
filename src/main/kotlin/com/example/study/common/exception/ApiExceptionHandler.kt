package com.example.study.common.exception

import com.example.study.common.Slf4j2KotlinLogging.log
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(ConstraintViolationException::class)
    private fun handlerConstraintViolationException(exception: ConstraintViolationException): ApiResponse<Nothing> {
        log.warn(exception.message)
        return ApiResponse.fail(errorCode = ErrorCode.E400_BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    private fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException): ApiResponse<Nothing> {
        log.warn(exception.message)
        val rootCause = exception.rootCause
        if (rootCause is MismatchedInputException) {
            val missingField = rootCause.path?.lastOrNull()?.fieldName ?: "unknown"
            return ApiResponse.fail(ErrorCode.E400_BAD_REQUEST, "Parameter ($missingField) is missing or invalid.")
        }

        return ApiResponse.fail(ErrorCode.E400_BAD_REQUEST)
    }

    @ExceptionHandler(AuthException::class)
    private fun authException(exception: AuthException): ApiResponse<Nothing> {
        var errorCode: ErrorCode = ErrorCode.E500_INTERNAL_SERVER_ERROR
        if (exception is TokenNotFoundException ||
            exception is TokenInvalidException
        ) {
            log.warn(exception.message)
            errorCode = ErrorCode.E401_UNAUTHORIZED
        }

        return ApiResponse.fail(errorCode = errorCode)
    }

    @ExceptionHandler(ApiException::class)
    private fun handleBaseException(exception: ApiException): ApiResponse<Nothing> {
        val errorCode = exception.errorCode
        if (errorCode.httpStatus.is4xxClientError) {
            log.warn(exception.resultErrorMessage)
        } else {
            log.error(exception.resultErrorMessage, exception)
        }
        return ApiResponse.fail(errorCode = errorCode)
    }
}