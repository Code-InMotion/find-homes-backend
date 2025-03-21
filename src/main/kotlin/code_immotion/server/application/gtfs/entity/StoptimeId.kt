package code_immotion.server.application.gtfs.entity

import java.io.Serializable

data class StopTimeId(
    var trip: String? = null,
    var stop: String? = null,
    var stopSequence: Int? = null,
) : Serializable