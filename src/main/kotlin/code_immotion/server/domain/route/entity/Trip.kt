package code_immotion.server.domain.route.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.springframework.data.domain.Persistable
import java.util.*

@Entity
class Trip(
    @Id
    private val id: String,
    @ManyToOne
    val route: Route,
    val serviceId: String
): Persistable<String> {
    override fun getId() = id

    override fun isNew() = true
}