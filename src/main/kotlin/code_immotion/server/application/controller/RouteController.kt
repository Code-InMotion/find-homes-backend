package code_immotion.server.application.controller

import code_immotion.server.domain.route.RouteService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "ROUTE API")
@RestController
@RequestMapping("route")
class RouteController(private val routeService: RouteService) {
    @PostMapping
    @Operation(summary = "경로 탐색")
    fun findRegionWithCondition(
        @RequestParam start: String,
        @RequestParam arrive: String
    ) = routeService.findRouteMinTime(start,arrive)
}