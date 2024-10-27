package code_immotion.server.property.entity

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "property")
class Rent(
    val deposit: Int,
    val monthlyAmount: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    state: String,
    city: String,
    buildingType: BuildingType,
    buildYear: Int,
    exclusiveArea: Int,
    floor: Int,
    dealDate: LocalDate,
) : Property(
    state = state,
    city = city,
    buildingType = buildingType,
    buildYear = buildYear,
    exclusiveArea = exclusiveArea,
    floor = floor,
    dealDate = dealDate,
)