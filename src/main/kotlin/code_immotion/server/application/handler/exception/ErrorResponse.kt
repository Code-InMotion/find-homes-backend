package code_immotion.server.application.handler.exception

import org.springframework.http.HttpStatusCode

class ErrorResponse(

    var statusCode: HttpStatusCode,
    var error: String,
    val message: MutableList<String> = ArrayList()
) {

    fun ErrorResponse(errorCode: ErrorCode) {
        this.statusCode = errorCode.httpStatus
        this.error = errorCode.httpStatus.name
        errorCode.message?.let { message.add(it) }
    }

    fun ErrorResponse(errorCode: ErrorCode, message: String) {
        this.statusCode = errorCode.httpStatus
        this.error = errorCode.httpStatus.name
        this.message.add(message)
    }

    fun ErrorResponse(errorCode: ErrorCode, message: List<String>?) {
        this.statusCode = errorCode.httpStatus
        this.error = errorCode.httpStatus.name
        this.message.addAll(message!!)
    }
}