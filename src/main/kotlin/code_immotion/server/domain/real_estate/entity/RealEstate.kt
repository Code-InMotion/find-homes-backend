package code_immotion.server.domain.real_estate.entity

import jakarta.persistence.*

@Entity
class RealEstate(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    val buildingName: String,
    val address: String,
    val addressNumber: String,
    val buildYear: Int,
    val exclusiveArea: Int,

    @OneToMany(mappedBy = "realEstate")
    val sales: MutableList<Sale> = mutableListOf(),

    @OneToMany(mappedBy = "realEstate")
    val rents: MutableList<Rent> = mutableListOf()
)