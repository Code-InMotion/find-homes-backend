package code_immotion.server.property

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("property")
class PropertyController(
    private val propertyService: PropertyService
) {
}