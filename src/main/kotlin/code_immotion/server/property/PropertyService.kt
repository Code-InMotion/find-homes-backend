package code_immotion.server.property

import code_immotion.server.property.entity.Property
import org.springframework.stereotype.Service

@Service
class PropertyService(private val propertyRepository: PropertyRepository) {
    fun readAll(): List<Property> = propertyRepository.findAll()

    fun saveAll(properties: List<Property>) = propertyRepository.saveAll(properties)

    fun deleteAll() = propertyRepository.deleteAll()
}