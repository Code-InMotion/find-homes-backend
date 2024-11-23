package code_immotion.server.property

import code_immotion.server.property.dto.PropertyPagingParam
import code_immotion.server.property.entity.Property
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger { }

@Service
class PropertyService(private val propertyRepository: PropertyRepository) {
    fun pagingProperties(pagingParam: PropertyPagingParam): PageImpl<Property> =
        propertyRepository.pagingProperties(pagingParam)

    fun saveAll(properties: List<Property>) = propertyRepository.saveAll(properties)

    fun upsertAll(properties: List<Property>) = propertyRepository.upsertAll(properties)

    fun findTotalSize(): Long = propertyRepository.findTotalSize()

    fun findAllByAddresses(addresses: List<String>) = propertyRepository.findAllByAddresses(addresses)


    fun deleteAll() = propertyRepository.deleteAll()
}