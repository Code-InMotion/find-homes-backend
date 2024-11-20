package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDate

sealed class Trade {
    abstract val type: TradeType
    abstract val floor: Int
    abstract val price: Long
    abstract val dealDate: LocalDate

    data class Sale(
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

    data class MonthlyRent(
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

    data class DepositRent(
        override val type: TradeType = TradeType.LONG_TERM_RENT,
        override val price: Long,
        override val floor: Int,
        override val dealDate: LocalDate,
    ) : Trade() {
        companion object {
            fun from(jsonNode: JsonNode) = DepositRent(
                price = jsonNode.path("deposit").asText().replace(",", "").toLong(),
                floor = jsonNode.path("floor").asInt(),
                dealDate = LocalDate.of(
                    jsonNode.path("dealYear").asInt(),
                    jsonNode.path("dealMonth").asInt(),
                    jsonNode.path("dealDay").asInt()
                )
            )
        }
    }
}