package code_immotion.server.domain.route.dto

data class RouteResponse(
    val destinationId: Long? = null,
    val duration: Int = 0,
    val walkDistance: Double = 0.0,
    val transfers: Int = 0,
    val fare: Int = 0,
    val legs: List<RouteLeg> = emptyList()
)
