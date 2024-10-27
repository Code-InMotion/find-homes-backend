package code_immotion.server.open_api

import org.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("open-api")
class OpenApiController(
    private val propertyOpenApiClient: PropertyOpenApiClient
) {
    @GetMapping("test")
    fun test2(): JSONObject {
        val result= propertyOpenApiClient.syncPropertiesWithOpenApi()
        return result
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun syncPropertiesWithOpenApi(
    ) {

    }
}