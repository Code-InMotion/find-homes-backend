package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate

@Document(collection = "property")
class MonthlyRent(
    val id: String? = null,
    val deposit: Long,
    val monthlyRent: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,

    @Field("property")
    val property: Property

) : Property() {
    companion object {
        fun from(jsonNode: JsonNode, state: String, city: String, houseType: HouseType): MonthlyRent {
            val property = Property.from(jsonNode, state, city, houseType)
            return MonthlyRent(
                id = null,
                deposit = jsonNode.path("deposit").asText().replace(",", "").toLong(),
                monthlyRent = jsonNode.path("monthlyAmount").asText().replace(",", "").toLong(),
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                property = property,
            )
        }
    }
}