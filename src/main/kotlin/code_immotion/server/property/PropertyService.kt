package code_immotion.server.property

import code_immotion.server.property.entity.Property
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service

@Service
class PropertyService(private val propertyRepository: PropertyRepository) {
    fun pagingProperties(pagingParam: PropertyPagingParam): PageImpl<Property> = propertyRepository.pagingProperties(pagingParam)

    fun saveAll(properties: List<Property>) = propertyRepository.bulkInsert(properties)

    fun deleteAll() = propertyRepository.deleteAll()
}