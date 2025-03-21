package code_immotion.server.application.gtfs.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.springframework.data.domain.Persistable
import java.time.LocalDateTime
import java.util.*

@Entity
class Trip(
    @Id
    private val id: String,
    @ManyToOne
    val route: Route,
    val serviceId: String,
    val updatedAt: LocalDateTime = LocalDateTime.now()
): Persistable<String> {
    override fun getId() = id

    override fun isNew() = true
}