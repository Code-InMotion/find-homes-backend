package code_immotion.server.property

import code_immotion.server.property.dto.PropertyPagingParam
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger { }

@Tag(name = "PROPERTY API")
@RestController
@RequestMapping("property")
class PropertyController(private val propertyService: PropertyService) {
    @GetMapping
    @PageableAsQueryParam
    @Operation(summary = "사용자 조건에 맞춘 매물 목록 조회")
    fun readAll(@ParameterObject pagingParam: PropertyPagingParam) = propertyService.pagingProperties(pagingParam)

    @GetMapping("size")
    @Operation(summary = "전체 매물 수 확인")
    fun findTotalSize() = propertyService.findTotalSize()

    @DeleteMapping
//    @Hidden
    fun deleteAll() = propertyService.deleteAll()

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleResponseStatusException(exception: IllegalArgumentException): String? {
        logger.error { exception.message }
        return exception.message
    }
}