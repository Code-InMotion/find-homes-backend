package code_immotion.server.property.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "property")
abstract class Property(
    @Id
    var id: String? = null,
    val address: String,
//    val state: String,
//    val city: String,
//    val district: String,
//    val jibun: String,
    val price: Long,
    val houseType: HouseType,
    val buildYear: Int,
    val exclusiveArea: Int,
    val floor: Int,
    val dealDate: LocalDate,
)