package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "property")
open class Property(
    val id: String? = null,
    val address: String,
    val houseType: HouseType,
    val buildYear: Int,
    val exclusiveArea: Int,

    @Field("trade")
    val trade: Trade,
    val latitude: Double? = null, // 위도
    val longitude: Double? = null, // 경도
) {
    companion object {
        fun from(jsonNode: JsonNode, state: String, city: String, houseType: HouseType): Property {
            val amount = jsonNode.path("dealAmount")
            val monthlyPrice = jsonNode.path("monthlyRent")

            val trade = when {
                !amount.isMissingNode -> Trade.Sale.from(jsonNode)
                monthlyPrice.asLong() != 0L -> Trade.MonthlyRent.from(jsonNode)
                else -> Trade.DepositRent.from(jsonNode)
            }
            return Property(
                address = "$state $city ${jsonNode.path("umdNm").asText()} ${jsonNode.path("jibun").asText()}",
                houseType = houseType,
                buildYear = jsonNode.path("buildYear").asInt(),
                exclusiveArea = jsonNode.path("excluUseAr").asDouble().toInt(),
                trade = trade
            )
        }
    }
}