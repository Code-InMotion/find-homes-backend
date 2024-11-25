package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.mongodb.core.index.CompoundIndex
import java.time.LocalDate

class DepositRent(
    id: String? = null,
    address: String,
    houseType: HouseType,
    type: TradeType,
    floor: Int,
    price: Long,
    dealDate: LocalDate,
    buildYear: Int,
    exclusiveArea: Int,
) : Property(null, address, houseType, type, floor, price, null, dealDate, buildYear, exclusiveArea) {
    companion object {
        fun from(jsonNode: JsonNode, state: String, city: String, houseType: HouseType) = DepositRent(
            address = "$state $city ${jsonNode.path("umdNm").asText()} ${jsonNode.path("jibun").asText()}",
            houseType = houseType,
            type = TradeType.LONG_TERM_RENT,
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