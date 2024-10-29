package code_immotion.server.open_api

import code_immotion.server.open_api.client.ApiLink
import code_immotion.server.open_api.client.OpenApiClient
import code_immotion.server.open_api.client.TransactionType
import code_immotion.server.property.PropertyService
import org.springframework.stereotype.Component

@Component
class OpenApiFacade(
    private val openApiClient: OpenApiClient,
    private val propertyService: PropertyService
) {
    fun syncPropertiesWithOpenApi() {
        val properties = openApiClient.sendRequest(ApiLink.APARTMENT, TransactionType.RENT, 11110, 202409)
        properties?.let { openApiClient.parseSalesFromXml(it) }
    }
}