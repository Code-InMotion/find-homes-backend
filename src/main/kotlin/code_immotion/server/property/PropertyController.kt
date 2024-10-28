package code_immotion.server.property

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("property")
class PropertyController(private val propertyService: PropertyService) {
    @GetMapping
    fun readAll() = propertyService.readAll()

    @DeleteMapping
    fun deleteAll() = propertyService.deleteAll()
}