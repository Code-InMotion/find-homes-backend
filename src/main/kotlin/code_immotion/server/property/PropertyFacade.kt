package code_immotion.server.property

import org.springframework.stereotype.Component

@Component
class PropertyFacade(
    private val propertyService: PropertyService
)