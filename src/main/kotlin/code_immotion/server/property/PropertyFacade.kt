package code_immotion.server.property

import code_immotion.server.open_api.client.OpenApiClient
import code_immotion.server.property.dto.PropertyAggregation
import code_immotion.server.property.dto.PropertyCondition
import org.springframework.stereotype.Component

@Component
class PropertyFacade(
    private val propertyService: PropertyService,
    private val openApiClient: OpenApiClient
) {
    fun findRegionWithCondition(condition: PropertyCondition): List<PropertyAggregation> {
        val rootNode = openApiClient.sendRequestForGeoLocation(condition.destination)
        if (rootNode.isEmpty) {
            throw Exception("잘못된 주소입니다")
        }
        val latitude: Double = rootNode.first().path("y").asText().toDouble()
        val longitude: Double = rootNode.first().path("x").asText().toDouble()
        return propertyService.findRegionWithCondition(condition, latitude, longitude)
    }

    fun findTotalSize() = propertyService.findTotalSize()

    fun deleteAll() = propertyService.deleteAll()
}