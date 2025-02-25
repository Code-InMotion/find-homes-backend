package code_immotion.server.domain.route.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.domain.Persistable
import java.util.UUID

@Entity
class Agency(
    @Id
    private val id: String,
    val name: String,
    val url: String,
    val timezone: String
) : Persistable<String> {
    override fun getId() = id

    override fun isNew() = true
}