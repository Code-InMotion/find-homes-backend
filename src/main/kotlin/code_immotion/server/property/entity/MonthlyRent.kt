package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDate

class MonthlyRent(
    val monthlyPrice: Long,
    override val type: TradeType = TradeType.MONTHLY_RENT,
    override val price: Long,
    override val floor: Int,
    override val dealDate: LocalDate,
) : Trade() {
    companion object {
        fun from(jsonNode: JsonNode) = MonthlyRent(
            price = jsonNode.path("deposit").asText().replace(",", "").toLong(),
            monthlyPrice = jsonNode.path("monthlyRent").asText().replace(",", "").toLong(),
            floor = jsonNode.path("floor").asInt(),
            dealDate = LocalDate.of(
                jsonNode.path("dealYear").asInt(),
                jsonNode.path("dealMonth").asInt(),
                jsonNode.path("dealDay").asInt()
            )
        )
    }
}