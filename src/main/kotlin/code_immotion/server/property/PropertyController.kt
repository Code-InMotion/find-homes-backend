package code_immotion.server.property

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "PROPERTY API")
@RestController
@RequestMapping("property")
class PropertyController(private val propertyService: PropertyService) {
    @GetMapping
    fun readAll() = propertyService.readAll()

    @GetMapping("size")
    fun readPropertiesSize() = propertyService.readAll().size

    @DeleteMapping
    fun deleteAll() = propertyService.deleteAll()
}