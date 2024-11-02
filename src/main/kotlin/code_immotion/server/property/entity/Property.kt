package code_immotion.server.property.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "property")
abstract class Property(
    @Id
    var id: String? = null,
    val state: String,
    val city: String,
    val district: String,
    val jibun: String,
    val buildingType: BuildingType,
    val buildYear: Int,
    val exclusiveArea: Int,
    val floor: Int,
    val dealDate: LocalDate,
)