package code_immotion.server.application.handler.exception

import org.springframework.http.HttpStatusCode

class ErrorResponse(
    private var statusCode: HttpStatusCode,
    private var error: String,
    private val message: MutableList<String> = ArrayList()
) {
    constructor(errorCode: ErrorCode, errorMessage: String) : this(
        errorCode.httpStatus,
        errorCode.httpStatus.name
    ) {
        message.add(errorMessage)
    }

    constructor(errorCode: ErrorCode, errorMessages: List<String>) : this(
        errorCode.httpStatus,
        errorCode.httpStatus.name
    ) {
        message.addAll(errorMessages)
    }
}