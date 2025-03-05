package code_immotion.server.application.handler

import code_immotion.server.application.handler.exception.CustomException
import code_immotion.server.application.handler.exception.ErrorCode
import code_immotion.server.application.handler.exception.ErrorResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger { }

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception, request: HttpServletRequest?): ErrorResponse {
        logger.error { "[Exception] : $exception" }
        return ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, exception.message!!)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(exception: RuntimeException, request: HttpServletRequest?): ErrorResponse {
        logger.error { "[Runtime] : $exception" }
        return ErrorResponse(ErrorCode.RUNTIME, exception.message!!)
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(exception: CustomException, request: HttpServletRequest?): ErrorResponse {
        logger.error { "[${exception.javaClass.simpleName}] : ${exception.message}" }
        return ErrorResponse(exception.errorCode, exception.message!!)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleException(exception: MethodArgumentNotValidException, request: HttpServletRequest?): ErrorResponse {
        val errorMessages = exception.bindingResult
            .allErrors
            .filterIsInstance<FieldError>()
            .mapNotNull { it.defaultMessage }

        logger.error { "[MethodArgumentNotValidException] : $errorMessages" }
        return ErrorResponse(ErrorCode.BAD_REQUEST, errorMessages)
    }
}