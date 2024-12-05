package code_immotion.server.application.handler.exception

class CustomException : RuntimeException {
    private val errorCode: ErrorCode

    constructor(errorCode: ErrorCode) : super(errorCode.message) {
        this.errorCode = errorCode
    }

    constructor(errorCode: ErrorCode, e: Exception) : super(e.message) {
        this.errorCode = errorCode
    }
}