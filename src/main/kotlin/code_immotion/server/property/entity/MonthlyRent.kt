package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

class MonthlyRent(
    val id: String? = null,
    val monthlyPrice: Long,
    address: String,
    houseType: HouseType,
    type: TradeType,
    floor: Int,
    price: Long,
    dealDate: LocalDate,
    buildYear: Int,
    exclusiveArea: Int,
    latitude: Double? = null, // 위도
    longitude: Double? = null, // 경도
) : Property(address, houseType, type, floor, price, dealDate, buildYear, exclusiveArea) {
    companion object {
        fun from(jsonNode: JsonNode, state: String, city: String, houseType: HouseType) = MonthlyRent(
            monthlyPrice = jsonNode.path("monthlyRent").asText().replace(",", "").toLong(),
            address = "$state $city ${jsonNode.path("umdNm").asText()} ${jsonNode.path("jibun").asText()}",
            houseType = houseType,
            type = TradeType.MONTHLY_RENT,
            floor = jsonNode.path("floor").asInt(),
            price = jsonNode.path("deposit").asText().replace(",", "").toLong(),
            buildYear = jsonNode.path("buildYear").asInt(),
            exclusiveArea = jsonNode.path("excluUseAr").asDouble().toInt(),
            dealDate = LocalDate.of(
                jsonNode.path("dealYear").asInt(),
                jsonNode.path("dealMonth").asInt(),
                jsonNode.path("dealDay").asInt()
            )
        )
    }
}