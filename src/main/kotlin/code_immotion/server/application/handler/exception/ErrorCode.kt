package code_immotion.server.application.handler.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val message: String?
) {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    BAD_REQUEST_PROPERTY(HttpStatus.BAD_REQUEST, "잘못된 매물 정보입니다."),
    WRONG_BUILDING_NAME(HttpStatus.BAD_REQUEST, "잘못된 건물명입니다."),
    INVALID_LOCATION(HttpStatus.NOT_FOUND, "잘못된 좌표 정보입니다."),

    NOT_FOUND_REGION(HttpStatus.NOT_FOUND, "조건에 맞는 지역을 찾을 수 없습니다."),
    NOT_FOUND_PROPERTY(HttpStatus.NOT_FOUND, "해당 매물을 찾을 수 없습니다."),
    NOT_FOUND_ROUTE(HttpStatus.NOT_FOUND, "경로 탐색에 실패했습니다."),

    OPEN_API_KAKAO_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "kakao geolocation에 실패했습니다."),
    OPEN_API_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "공공데이터 포털에서 데이터를 얻어오는데 실패했습니다."),
    RUNTIME(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버에러입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버에러입니다."),
    ;
}