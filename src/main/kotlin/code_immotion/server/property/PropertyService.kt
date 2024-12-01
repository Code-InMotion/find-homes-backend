package code_immotion.server.property

import code_immotion.server.property.dto.PropertyAggregation
import code_immotion.server.property.dto.PropertyCondition
import code_immotion.server.property.dto.PropertyResponse
import code_immotion.server.property.entity.Property
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.geo.Point
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger { }

@Service
class PropertyService(private val propertyRepository: PropertyRepository) {
    fun findRegionWithCondition(condition: PropertyCondition, point: Point): List<PropertyAggregation> =
        propertyRepository.findRegionWithCondition(condition, point)

    fun findProperty(propertyId: String, point: Point): PropertyResponse {
        val property = propertyRepository.findProperty(propertyId, point)
        return property ?: throw Exception("해당 매물을 찾을 수 없습니다.")
    }

    fun upsertAll(properties: List<Property>) = propertyRepository.upsertAll(properties)

    fun findTotalSize(): Long = propertyRepository.findTotalSize()

    fun deleteAll() = propertyRepository.deleteAll()
}