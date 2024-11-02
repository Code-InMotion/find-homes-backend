package code_immotion.server.open_api

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("open-api")
class OpenApiController(
    private val openApiFacade: OpenApiFacade
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun syncPropertiesWithOpenApi(@RequestParam("dealMonth") dealMonth: Int) = openApiFacade.syncPropertiesWithOpenApi(dealMonth)

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(exception: ResponseStatusException): ResponseStatusException {
        logger.error { exception.message }
        return exception
    }
}