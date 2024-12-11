package code_immotion.server.domain.property.entity

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDate

class MonthlyRent(
    id: String? = null,
    rentPrice: Long,
    address: String,
    addressNumber: String,
    houseType: HouseType,
    tradeType: TradeType,
    floor: Int,
    price: Long,
    dealDate: LocalDate,
    buildYear: Int,
    exclusiveArea: Int,
) : Property(id, address, addressNumber, houseType, tradeType, floor, price, rentPrice, dealDate, buildYear, exclusiveArea) {
    companion object {
        fun from(jsonNode: JsonNode, state: String, city: String, houseType: HouseType) = MonthlyRent(
            rentPrice = jsonNode.path("monthlyRent").asText().replace(",", "").toLong(),
            address = "$state $city ${jsonNode.path("umdNm").asText()}",
            addressNumber = jsonNode.path("jibun").asText(),
            houseType = houseType,
            tradeType = TradeType.MONTHLY_RENT,
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