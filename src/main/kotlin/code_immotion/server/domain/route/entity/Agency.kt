package code_immotion.server.domain.route.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Agency(
    @Id
    val id: String,
    val name: String,
    val url: String,
    val timezone: String
)