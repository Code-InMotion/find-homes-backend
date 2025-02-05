package code_immotion.server.domain.route.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class Route(
    @Id
    val id: String,
    @ManyToOne
    val agency: Agency,
    val shortName: String, // route_short_name
    val longName: String,  // route_long_name
    val type: Int // KTDB 기준: 0(시내버스), 1(도시철도), 2(해운), 3(시외버스), 4(일반철도), 5(공항버스), 6(고속철도), 7(항공)
)