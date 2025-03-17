package code_immotion.server.domain.property.entity

import jakarta.persistence.Embeddable

@Embeddable
class GeoLocation(
    val longitude: Double,
    val latitude: Double
)