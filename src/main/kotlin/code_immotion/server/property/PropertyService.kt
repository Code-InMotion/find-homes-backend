package code_immotion.server.property

import code_immotion.server.property.dto.PropertyPagingParam
import code_immotion.server.property.entity.Property
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger { }

@Service
class PropertyService(private val propertyRepository: PropertyRepository) {
    fun pagingProperties(pagingParam: PropertyPagingParam): PageImpl<Property> = propertyRepository.pagingProperties(pagingParam)

    fun saveAll(properties: List<Property>) = propertyRepository.bulkInsert(properties)

    fun readSize(): Int {
        val size = propertyRepository.findAll().size
        logger.info { "size: $size" }
        return size
    }

    fun readOne(address: String) = propertyRepository.findByAddress(address)


    fun deleteAll() = propertyRepository.deleteAll()
}