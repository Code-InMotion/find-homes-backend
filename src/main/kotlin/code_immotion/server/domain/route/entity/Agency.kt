package code_immotion.server.domain.route.entity

import jakarta.persistence.Entity
import org.springframework.data.annotation.Id

@Entity
class Agency(
    @Id
    val id: String = "A1",
    val name: String = "KTDB",
    val url: String = "http://www.ktdb.go.kr/",
    val timezone: String = "Japan"
)