package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "property")
open class Property(
    val address: String,
    val houseType: HouseType,
    val type: TradeType,
    val floor: Int,
    val price: Long,
    val dealDate: LocalDate,
    val buildYear: Int,
    val exclusiveArea: Int,
    val latitude: Double? = null, // 위도
    val longitude: Double? = null, // 경도
) {
    companion object {
        fun from(jsonNode: JsonNode, state: String, city: String, houseType: HouseType): Property {
            val amount = jsonNode.path("dealAmount")
            val monthlyPrice = jsonNode.path("monthlyRent")

            return when {
                !amount.isMissingNode -> Sale.from(jsonNode, state, city, houseType)
                monthlyPrice.asLong() != 0L -> MonthlyRent.from(jsonNode, state, city, houseType)
                else -> DepositRent.from(jsonNode, state, city, houseType)
            }
        }
    }
}