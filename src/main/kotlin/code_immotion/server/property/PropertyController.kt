package code_immotion.server.property

import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "PROPERTY API")
@RestController
@RequestMapping("property")
class PropertyController(private val propertyService: PropertyService) {
    @GetMapping
    @PageableAsQueryParam
    fun readAll(@ParameterObject pagingParam: PropertyPagingParam) = propertyService.pagingProperties(pagingParam)

    @DeleteMapping
    fun deleteAll() = propertyService.deleteAll()
}