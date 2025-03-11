package code_immotion.server.domain.old_property.dto

import code_immotion.server.domain.old_property.entity.HouseType
import code_immotion.server.domain.old_property.entity.Property
import code_immotion.server.domain.old_property.entity.TradeType
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import java.time.LocalDate

data class PropertyResponse(
    var id: String?,

    @field:Schema(description = "건물명", example = "롯데캐슬 클라시엘")
    val buildingName: String,

    @field:Schema(description = "매물 주소", example = "경기도 용인시 수지구 성복동")
    val address: String,

    @field:Schema(description = "매물 주소 지번", example = "11-2")
    val addressNumber: String,

    @field:Schema(description = "거주 유형", example = "APARTMENT")
    val houseType: HouseType,

    @field:Schema(description = "거래 유형", example = "SALE")
    val tradeType: TradeType,

    @field:Schema(description = "매물 층", example = "3")
    val floor: Int,

    @field:Schema(description = "매매 / 보증금", example = "300000000")
    val price: Long,

    @field:Schema(description = "월세", example = "300000")
    val rentPrice: Long?,

    @field:Schema(description = "거래날짜", example = "2023-12-27T15:00:00.000+00:00")
    val dealDate: LocalDate,

    @field:Schema(description = "건축 년도", example = "2023-12-27T15:00:00.000+00:00")
    val buildYear: Int,

    @field:Schema(description = "전용 면적", example = "45")
    val exclusiveArea: Int,

    @field:Schema(description = "목적지까지 거리", example = "12.3456789")
    val distance: Double,

    @field:Schema(description = "좌표")
    val location: Point
) {
    @field:Schema(description = "대중교통을 사용한 이동 소요 시간(60으로 고정 추가 개발 예정)", example = "60")
    val travelTime: Int = 60

    companion object {
        fun from(property: Property, distance: Distance): PropertyResponse {
            return PropertyResponse(
                id = property.id!!,
                buildingName = property.buildingName,
                address = property.address,
                addressNumber = property.addressNumber,
                houseType = property.houseType,
                tradeType = property.tradeType,
                floor = property.floor,
                price = property.price,
                rentPrice = property.rentPrice,
                dealDate = property.dealDate,
                buildYear = property.buildYear,
                exclusiveArea = property.buildYear,
                location = property.location!!,
                distance = distance.value,
            )
        }
    }
}