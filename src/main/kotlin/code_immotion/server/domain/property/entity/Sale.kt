package code_immotion.server.domain.property.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Sale(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    val floor: Int,
    val houseType: HouseType,
    val price: Long,
    val dealDate: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    val property: Property
)