package code_immotion.server.property

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service

@Service
class PropertyService(
    private val propertyRepository: PropertyRepository,
    private val entityManager: EntityManager
) {
}