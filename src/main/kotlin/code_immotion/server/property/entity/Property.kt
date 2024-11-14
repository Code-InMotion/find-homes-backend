package code_immotion.server.property.entity

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "property")
open class Property(
    val address: String,
    val houseType: HouseType,
    val buildYear: Int,
    val exclusiveArea: Int,
    val floor: Int,
    val dealDate: LocalDate,
    val latitude: Double? = null, // 위도
    val longitude: Double? = null, // 경도
)