package code_immotion.server.application.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger { }

@Tag(name = "GTFS 데이터 API")
@RestController
@ResponseStatus(HttpStatus.CREATED)
@RequestMapping("gtfs")
class GtfsController {

    @PostMapping("subway")
    fun createSubwayGtfs() {

    }
}