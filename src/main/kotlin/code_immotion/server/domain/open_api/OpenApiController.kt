package code_immotion.server.domain.open_api

import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

private val logger = KotlinLogging.logger {}

@Tag(name = "공공데이터 API")
@RestController
@RequestMapping("open-api")
class OpenApiController(
    private val openApiFacade: code_immotion.server.domain.open_api.OpenApiFacade
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Open Api 호출")
    suspend fun syncPropertiesWithOpenApi(@RequestParam("dealMonth") dealMonth: Int) =
        openApiFacade.syncPropertiesWithOpenApi(dealMonth)

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(exception: ResponseStatusException): ResponseStatusException {
        code_immotion.server.domain.open_api.logger.error { exception.message }
        return exception
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): Exception {
        code_immotion.server.domain.open_api.logger.error { exception.printStackTrace() }
        return exception
    }
}