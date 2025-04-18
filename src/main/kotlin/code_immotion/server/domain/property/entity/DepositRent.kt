package code_immotion.server.domain.property.entity

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDate

class DepositRent(
    id: String? = null,
    buildingName: String,
    address: String,
    addressNumber: String,
    houseType: HouseType,
    tradeType: TradeType,
    floor: Int,
    price: Long,
    dealDate: LocalDate,
    buildYear: Int,
    exclusiveArea: Int,
) : Property(id, buildingName, address, addressNumber, houseType, tradeType, floor, price, null, dealDate, buildYear, exclusiveArea) {
    companion object {
        fun from(jsonNode: JsonNode, state: String, city: String, houseType: HouseType, buildingName: String) = DepositRent(
            buildingName = buildingName,
            address = "$state $city ${jsonNode.path("umdNm").asText()}",
            addressNumber = jsonNode.path("jibun").asText(),
            houseType = houseType,
            tradeType = TradeType.LONG_TERM_RENT,
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