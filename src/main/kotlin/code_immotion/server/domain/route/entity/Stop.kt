package code_immotion.server.domain.route.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Stop(
    @Id
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double
)