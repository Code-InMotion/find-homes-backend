package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "property")
class Sale(
    val id: String? = null,
    val price: Long,

    @Field("property")
    val property: Property
) : Property() {
    companion object {
        fun from(jsonNode: JsonNode, state: String, city: String, houseType: HouseType): Sale {
            val property = Property.from(jsonNode, state, city, houseType)
            return Sale(
                id = null,
                price = jsonNode.path("dealAmount").asText().replace(",", "").toLong(),
                property = property
            )
        }
    }
}