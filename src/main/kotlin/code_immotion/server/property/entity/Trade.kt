package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate

sealed class Trade {
    abstract val type: TradeType
    abstract val floor: Int
    abstract val price: Long
    abstract val dealDate: LocalDate

    @Document("property")
//    @TypeAlias("Sale")
    data class Sale(
        @Field("_type")
        override val type: TradeType = TradeType.SALE,
        @Field("_price")
        override val price: Long,
        @Field("_floor")
        override val floor: Int,
        @Field("_dealDate")
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

    @Document("property")
//    @TypeAlias("MonthlyRent")
    data class MonthlyRent(
        @Field("_monthlyPrice")
        val monthlyPrice: Long,
        @Field("_type")
        override val type: TradeType = TradeType.MONTHLY_RENT,
        @Field("_price")
        override val price: Long,
        @Field("_floor")
        override val floor: Int,
        @Field("_dealDate")
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

    @Document("property")
//    @TypeAlias("DepositRent")
    data class DepositRent(
        @Field("_type")
        override val type: TradeType = TradeType.LONG_TERM_RENT,
        @Field("_price")
        override val price: Long,
        @Field("_floor")
        override val floor: Int,
        @Field("_dealDate")
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