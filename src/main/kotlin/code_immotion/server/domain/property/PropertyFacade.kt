package code_immotion.server.domain.property

import code_immotion.server.application.handler.exception.CustomException
import code_immotion.server.application.handler.exception.ErrorCode
import code_immotion.server.domain.open_api.client.OpenApiClient
import code_immotion.server.domain.property.dto.PropertyAggregation
import code_immotion.server.domain.property.dto.PropertyCondition
import code_immotion.server.domain.property.dto.PropertyResponse
import org.springframework.data.geo.Point
import org.springframework.stereotype.Component

@Component
class PropertyFacade(
    private val propertyService: PropertyService,
    private val openApiClient: OpenApiClient
) {
    fun findRegionWithCondition(condition: PropertyCondition.Recommend): List<PropertyAggregation.Info> {
        val point = extractPoint(condition.destination)
        return propertyService.findRegionWithCondition(condition, point)
            .map { PropertyAggregation.Info.from(it) }
    }

    fun findRegionProperties(condition: PropertyCondition.Address): List<PropertyResponse> {
        val point = extractPoint(condition.destination)
        return propertyService.findRegionProperties(condition, point)
    }

    fun findProperty(propertyId: String, destination: String): PropertyResponse {
        val point = extractPoint(destination)
        return propertyService.findProperty(propertyId, point)
    }

    fun findTotalSize() = propertyService.findTotalSize()

    fun deleteAll() = propertyService.deleteAll()

    private fun extractPoint(destination: String): Point {
        val rootNode = openApiClient.sendRequestForGeoLocation(destination)

        val latitude: Double = rootNode.first().path("y").asText().toDouble()
        val longitude: Double = rootNode.first().path("x").asText().toDouble()

        return Point(longitude, latitude)
    }
}