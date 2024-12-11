package code_immotion.server.domain.property.dto

import code_immotion.server.domain.property.entity.HouseType
import code_immotion.server.domain.property.entity.TradeType
import io.swagger.v3.oas.annotations.media.Schema

class PropertyCondition {
    data class Recommend(
        @field:Schema(description = "매매 / 보증금 최소금액", example = "1000000")
        val minPrice: Long = 0,

        @field:Schema(description = "매매 / 보증금 최고금액", example = "10000000000")
        val maxPrice: Long = 10_000_000_000L,

        @field:Schema(example = "0", description = "월세 최소금액")
        val minRentPrice: Long = 0,

        @field:Schema(example = "100000000", description = "월세 최고금액")
        val maxRentPrice: Long = 100_000_000L,

        @field:Schema(description = "정렬 기준", implementation = SortType::class)
        val sortType: SortType = SortType.PRICE,

        @field:Schema(description = "이동 소요 시간(분 기준)")
        val travelTime: Int = 60,

        @field:Schema(example = "성복동 819", required = true, description = "목적지(회사/학교 등)")
        val destination: String,

        @field:Schema(description = "주거 형태", implementation = HouseType::class)
        val houseType: List<HouseType> = HouseType.entries.toList(),

        @field:Schema(description = "거래 형태", implementation = TradeType::class)
        val tradeType: List<TradeType> = TradeType.entries.toList()
    ) {
        init {
            require(maxPrice > minPrice) { "Maximum price must be greater than minimum price" }
        }

        companion object {
            fun from(condition: Address) = Recommend(
                minPrice = condition.minPrice,
                maxPrice = condition.maxPrice,
                minRentPrice = condition.minRentPrice,
                maxRentPrice = condition.maxRentPrice,
                sortType = condition.sortType,
                travelTime = condition.travelTime,
                destination = condition.destination,
                houseType = condition.houseType,
                tradeType = condition.tradeType
            )
        }
    }

    data class Address(
        @field:Schema(description = "매매 / 보증금 최소금액", example = "1000000")
        val minPrice: Long = 0,

        @field:Schema(description = "매매 / 보증금 최고금액", example = "10000000000")
        val maxPrice: Long = 10_000_000_000L,

        @field:Schema(example = "0", description = "월세 최소금액")
        val minRentPrice: Long = 0,

        @field:Schema(example = "100000000", description = "월세 최고금액")
        val maxRentPrice: Long = 100_000_000L,

        @field:Schema(description = "정렬 기준", implementation = SortType::class)
        val sortType: SortType = SortType.PRICE,

        @field:Schema(description = "이동 소요 시간(분 기준)")
        val travelTime: Int = 60,

        @field:Schema(example = "성복동 819", required = true, description = "목적지(회사/학교 등)")
        val destination: String,

        @field:Schema(required = false, description = "주거 형태", implementation = HouseType::class)
        val houseType: List<HouseType> = HouseType.entries.toList(),

        @field:Schema(required = false, description = "거래 형태", implementation = TradeType::class)
        val tradeType: List<TradeType> = TradeType.entries.toList(),

        @field:Schema(required = true, description = "추천 지역")
        val address: String
    )
}