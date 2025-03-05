package code_immotion.server.application.handler.exception

class CustomException(
     val errorCode: ErrorCode,
    message: String? = null
) : RuntimeException(message ?: errorCode.message)