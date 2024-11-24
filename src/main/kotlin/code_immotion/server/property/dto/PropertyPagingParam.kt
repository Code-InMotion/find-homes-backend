package code_immotion.server.property.dto

import code_immotion.server.property.entity.HouseType
import code_immotion.server.property.entity.TradeType
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class PropertyPagingParam(
    @Parameter(example = "0", required = true)
    val page: Int = 0,

    @Parameter(example = "10", required = true)
    val size: Int = 20,

    @Parameter(example = "1_000_000L", required = false, description = "매매 / 보증금 최고금액")
    val minPrice: Long = 0,

    @Parameter(example = "1_000_000L", required = false, description = "매매 / 보증금 최소금액")
    val maxPrice: Long = 100_000_000L,

    @Parameter(example = "1_000_000L", required = false, description = "월세 최고금액")
    val minRentPrice: Long = 0,

    @Parameter(example = "1_000_000L", required = false, description = "월세 최소금액")
    val maxRentPrice: Long = 100_000_000L,

    @Parameter(required = false, description = "주거 형태")
    houseType: List<HouseType>? = null,

    @Parameter(required = false, description = "거래 형태")
    tradeType: List<TradeType>? = null
) {
    val houseType: List<HouseType> = houseType ?: HouseType.entries
    val tradeType: List<TradeType> = tradeType ?: TradeType.entries

    init {
        require(maxPrice > minPrice) { "Maximum price must be greater than minimum price" }
    }

    fun toPageable(): Pageable {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "trade.dealDate"))
    }
}