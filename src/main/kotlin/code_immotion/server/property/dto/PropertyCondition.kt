package code_immotion.server.property.dto

import code_immotion.server.property.entity.HouseType
import code_immotion.server.property.entity.TradeType
import io.swagger.v3.oas.annotations.media.Schema

data class PropertyCondition(
    @field:Schema(description = "매매 / 보증금 최소금액", example = "1_000_000L")
    val minPrice: Long = 0,

    @field:Schema(description = "매매 / 보증금 최고금액", example = "10_000_000_000L")
    val maxPrice: Long = 10_000_000_000L,

    @field:Schema(example = "0", required = false, description = "월세 최소금액")
    val minRentPrice: Long = 0,

    @field:Schema(example = "100_000_000L", required = false, description = "월세 최고금액")
    val maxRentPrice: Long = 100_000_000L,

    @field:Schema(required = false, description = "정렬 기준")
    val sortType: SortType = SortType.PRICE,

    @field:Schema(required = false, description = "이동 소요 시간(분 기준)")
    val travelTime: Int,

    @field:Schema(example = "경기도 성남시 분당구 정지일로 95", required = true, description = "목적지(회사/학교 등)")
    val destination: String,

    @field:Schema(required = false, description = "주거 형태")
    val houseType: List<HouseType> = HouseType.entries.toList(),

    @field:Schema(required = false, description = "거래 형태")
    val tradeType: List<TradeType> = TradeType.entries.toList()
) {

    init {
        require(maxPrice > minPrice) { "Maximum price must be greater than minimum price" }
    }
}