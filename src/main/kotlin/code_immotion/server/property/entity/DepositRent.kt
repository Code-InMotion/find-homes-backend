package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate

@Document(collection = "property")
class DepositRent(
    val id: String? = null,
    val deposit: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,

    @Field("property")
    val property: Property
) : Property() {
    companion object {
        fun from(jsonNode: JsonNode, state: String, city: String, houseType: HouseType): DepositRent {
            val property = Property.from(jsonNode, state, city, houseType)
            return DepositRent(
                id = null,
                deposit = jsonNode.path("deposit").asText().replace(",", "").toLong(),
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                property = property
            )
        }
    }
}