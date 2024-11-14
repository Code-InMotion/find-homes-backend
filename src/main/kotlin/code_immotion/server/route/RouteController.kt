package code_immotion.server.route

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("open-api")
class RouteController(
    private val routeService: RouteService
)