package code_immotion.server.property

import code_immotion.server.property.dto.PropertyPagingParam
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.web.bind.annotation.*
import java.lang.IllegalArgumentException

private val logger = KotlinLogging.logger { }

@Tag(name = "PROPERTY API")
@RestController
@RequestMapping("property")
class PropertyController(private val propertyService: PropertyService) {
    @GetMapping
    @PageableAsQueryParam
    fun readAll(@ParameterObject pagingParam: PropertyPagingParam) = propertyService.pagingProperties(pagingParam)

    @GetMapping("size")
    fun readSize() = propertyService.readSize()

    @GetMapping("{address}")
    fun readOne(@PathVariable("address") address: String) = propertyService.readOne(address)

    @DeleteMapping
    fun deleteAll() = propertyService.deleteAll()

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleResponseStatusException(exception: IllegalArgumentException): String? {
        logger.error { exception.message }
        return exception.message
    }
}