package code_immotion.server.domain.route.entity

import java.io.Serializable

data class StopTimeId(
    var trip: String? = null,
    var stop: String? = null,
    var stopSequence: Int? = null,
) : Serializable