package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDate

class Sale(
    override val type: TradeType = TradeType.SALE,
    override val price: Long,
    override val floor: Int,
    override val dealDate: LocalDate,
) : Trade() {
    companion object {
        fun from(jsonNode: JsonNode) = Sale(
            price = jsonNode.path("dealAmount").asText().replace(",", "").toLong(),
            floor = jsonNode.path("floor").asInt(),
            dealDate = LocalDate.of(
                jsonNode.path("dealYear").asInt(),
                jsonNode.path("dealMonth").asInt(),
                jsonNode.path("dealDay").asInt()
            )
        )
    }
}