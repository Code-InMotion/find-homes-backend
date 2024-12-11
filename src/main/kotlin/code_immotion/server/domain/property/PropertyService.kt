package code_immotion.server.domain.property

import code_immotion.server.application.handler.exception.CustomException
import code_immotion.server.application.handler.exception.ErrorCode
import code_immotion.server.domain.property.dto.PropertyAggregation
import code_immotion.server.domain.property.dto.PropertyCondition
import code_immotion.server.domain.property.dto.PropertyResponse
import code_immotion.server.domain.property.entity.Property
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.geo.Point
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger { }

@Service
class PropertyService(private val propertyRepository: PropertyRepository) {
    fun findRegionWithCondition(condition: PropertyCondition.Recommend, point: Point): List<PropertyAggregation.Data> {
        return propertyRepository.findRegionWithCondition(condition, point).takeIf { it.isNotEmpty() }
            ?: throw CustomException(ErrorCode.NOT_FOUND_REGION)
    }


    fun findProperty(propertyId: String, point: Point): PropertyResponse {
        return propertyRepository.findProperty(propertyId, point)
            ?: throw CustomException(ErrorCode.NOT_FOUND_PROPERTY)
    }

    fun findRegionProperties(condition: PropertyCondition.Address, point: Point): List<PropertyResponse> {
        val recommendCondition = PropertyCondition.Recommend.from(condition)
        return findRegionWithCondition(recommendCondition, point)
            .first { it.address == condition.address }
            .properties
            .takeIf { it.isNotEmpty() }
            ?: throw CustomException(ErrorCode.NOT_FOUND_PROPERTY)
    }


    fun upsertAll(properties: List<Property>) = propertyRepository.upsertAll(properties)

    fun findTotalSize(): Long = propertyRepository.findTotalSize()

    fun deleteAll() = propertyRepository.deleteAll()
}