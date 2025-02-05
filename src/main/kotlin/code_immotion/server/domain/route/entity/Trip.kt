package code_immotion.server.domain.route.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class Trip(
    @Id
    val id: String, // 노선ID_Ord+001~NNN 형식
    @ManyToOne
    val route: Route,
    val serviceId: String
)