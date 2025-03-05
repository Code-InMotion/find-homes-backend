package code_immotion.server.domain.route.dto

import java.time.LocalDateTime

data class RouteLeg(
    val mode: String,
    val route: String?,
    val fromName: String,
    val toName: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val duration: Int,
    val distance: Double,
    val intermediateStops: List<StopResponse> = emptyList()
)
