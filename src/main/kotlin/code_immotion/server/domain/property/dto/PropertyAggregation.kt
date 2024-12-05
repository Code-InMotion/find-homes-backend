package code_immotion.server.domain.property.dto

import io.swagger.v3.oas.annotations.media.Schema

class PropertyAggregation {
    data class Info(
        @Schema(description = "지역 명")
        val address: String,
        @Schema(description = "매물 수")
        val propertyCount: Int,
        @Schema(description = "매물들의 매매/보증금 평균")
        val averagePrice: Double,
        @Schema(description = "목적지에서 매물까지의 평균 거리")
        val averageDistance: Double,
    ) {
        companion object {
            fun from(propertyAggregations: Data) =
                Info(
                    address = propertyAggregations.address,
                    propertyCount = propertyAggregations.propertyCount,
                    averagePrice = propertyAggregations.averagePrice,
                    averageDistance = propertyAggregations.averageDistance
                )
        }
    }

    data class Data(
        @Schema(description = "지역 명")
        val address: String,
        @Schema(description = "매물 수")
        val propertyCount: Int,
        @Schema(description = "매물들의 매매/보증금 평균")
        val averagePrice: Double,
        @Schema(description = "목적지에서 매물까지의 평균 거리")
        val averageDistance: Double,
        val properties: List<PropertyResponse>
    )
}