package code_immotion.server.domain.route.dto

import java.time.LocalDateTime

data class RouteRequest(
    val fromLat: Double,
    val fromLon: Double,
    val toLat: Double,
    val toLon: Double,
    val time: LocalDateTime,
    val arriveBy: Boolean = false
)
