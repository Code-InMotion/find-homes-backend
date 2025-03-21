package code_immotion.server.application.gtfs.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.springframework.data.domain.Persistable
import java.time.LocalDateTime
import java.util.*

@Entity
class Route(
    @Id
    private val id: String,
    @ManyToOne
    val agency: Agency,
    val shortName: String, // route_short_name
    val longName: String,  // route_long_name
    val type: Int, // KTDB 기준: 0(시내버스), 1(도시철도), 2(해운), 3(시외버스), 4(일반철도), 5(공항버스), 6(고속철도), 7(항공)
    val updatedAt: LocalDateTime = LocalDateTime.now()
): Persistable<String> {
    override fun getId() = id

    override fun isNew() = true
}