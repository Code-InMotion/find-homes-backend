package code_immotion.server.property

import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class PropertyPagingParam(
    @Parameter(example = "0", required = true)
    val page: Int = 0,

    @Parameter(example = "10", required = true)
    val size: Int = 20
) {
    fun toPageable(): Pageable {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"dealDate"))
    }
}