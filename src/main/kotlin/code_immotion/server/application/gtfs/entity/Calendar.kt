package code_immotion.server.application.gtfs.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.domain.Persistable
import java.time.LocalDate
import java.util.*

@Entity
class Calendar(
    @Id
    private val id: String,
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean,
    val startDate: LocalDate,
    val endDate: LocalDate
) : Persistable<String> {
    override fun getId() = id

    override fun isNew() = true
}