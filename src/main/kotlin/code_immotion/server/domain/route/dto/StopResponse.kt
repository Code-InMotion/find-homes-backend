package code_immotion.server.domain.route.dto

import java.time.LocalDateTime

data class StopResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val time: LocalDateTime
)
