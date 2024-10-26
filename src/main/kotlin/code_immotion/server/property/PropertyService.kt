package code_immotion.server.property

import org.springframework.stereotype.Service

@Service
class PropertyService(
    private val propertyRepository: PropertyRepository,
) {
}