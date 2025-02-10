package code_immotion.server.domain.route.entity

import java.io.Serializable

data class StopTimeId(
    val trip: Trip,
    val stop: Stop,
    val stopSequence: Int,
) : Serializable