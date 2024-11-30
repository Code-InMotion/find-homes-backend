package code_immotion.server.property

import code_immotion.server.property.dto.PropertyCondition
import code_immotion.server.property.dto.PropertyResponse
import code_immotion.server.property.entity.Property
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger { }

@Service
class PropertyService(private val propertyRepository: PropertyRepository) {
    fun pagingProperties(pagingParam: PropertyCondition, latitude: Double, longitude: Double): List<PropertyResponse> =
        propertyRepository.pagingProperties(pagingParam, latitude, longitude)

    fun upsertAll(properties: List<Property>) = propertyRepository.upsertAll(properties)

    fun createGeoIndex() =propertyRepository.createGeoIndex()

    fun findTotalSize(): Long = propertyRepository.findTotalSize()

    fun deleteAll() = propertyRepository.deleteAll()
}