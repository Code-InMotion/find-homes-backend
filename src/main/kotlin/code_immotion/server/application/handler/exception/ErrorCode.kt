package code_immotion.server.application.handler.exception

import org.springframework.http.HttpStatus
import java.util.function.Supplier

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val message: String?
) : Supplier<CustomException> {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.")
    ;

    override fun get(): CustomException {
        return CustomException(this)
    }
}