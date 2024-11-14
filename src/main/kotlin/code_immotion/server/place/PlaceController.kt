package code_immotion.server.place

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "PLACE API")
@RestController
@RequestMapping("places")
class PlaceController(
    private val placeService: PlaceService
) {
    @GetMapping
    fun readAll() = placeService.readAll()

    @DeleteMapping
    fun deleteAll() = placeService.deleteAll()
}