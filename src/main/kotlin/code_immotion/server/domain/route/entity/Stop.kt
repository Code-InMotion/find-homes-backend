package code_immotion.server.domain.route.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.domain.Persistable
import java.time.LocalDateTime
import java.util.*

@Entity
class Stop(
    @Id
    private val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val updatedAt: LocalDateTime = LocalDateTime.now()
): Persistable<String> {
    override fun getId() = id

    override fun isNew() = true
}