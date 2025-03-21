package code_immotion.server.application.controller

import code_immotion.server.application.api.ApiService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@Tag(name = "공공데이터 API")
@RestController
@RequestMapping("open-api")
class OpenApiController(
    private val httpService: ApiService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Open Api 호출")
    suspend fun syncPropertiesWithOpenApi(@RequestParam("dealMonth") dealMonth: Int) =
        httpService.syncPropertiesWithOpenApi(dealMonth)
}