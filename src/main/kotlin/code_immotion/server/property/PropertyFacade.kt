package code_immotion.server.property

import code_immotion.server.open_api.client.OpenApiClient
import code_immotion.server.property.dto.PropertyPagingParam
import code_immotion.server.property.entity.Property
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Component

@Component
class PropertyFacade(
    private val propertyService: PropertyService,
    private val openApiClient: OpenApiClient
) {
    fun pagingProperties(pagingParam: PropertyPagingParam): PageImpl<Property> {
        val rootNode = openApiClient.sendRequestForGeoLocation(pagingParam.destination).first()
        var latitude: Double = rootNode.path("y").asText().toDouble()
        var longitude: Double = rootNode.path("x").asText().toDouble()
        return propertyService.pagingProperties(pagingParam, latitude, longitude)
    }

    fun findTotalSize() = propertyService.findTotalSize()

    fun deleteAll() = propertyService.deleteAll()
}