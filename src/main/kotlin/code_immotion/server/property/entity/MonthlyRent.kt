package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "property")
class MonthlyRent(
    val rentPrice: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    price: Long,
    address: String,
    houseType: HouseType,
    buildYear: Int,
    exclusiveArea: Int,
    floor: Int,
    dealDate: LocalDate,
) : Property(
    price = price,
    address = address,
    houseType = houseType,
    buildYear = buildYear,
    exclusiveArea = exclusiveArea,
    floor = floor,
    dealDate = dealDate,
) {
    companion object {
        fun from(jsonNode: JsonNode, state: String, city: String, houseType: HouseType) = MonthlyRent(
            address = "$state $city ${jsonNode.path("umdNm").asText()} ${jsonNode.path("jibun").asText()}",
            houseType = houseType,
            buildYear = jsonNode.path("buildYear").asInt(),
            exclusiveArea = jsonNode.path("excluUseAr").asDouble().toInt(),
            floor = jsonNode.path("floor").asInt(),
            rentPrice = (jsonNode.path("monthlyAmount").asText().takeIf { it.isNotBlank() }?.replace(",", "")
                ?.toInt() ?: 0),
            price = jsonNode.path("deposit").asText().replace(",", "").toLong(),
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            dealDate = LocalDate.of(
                jsonNode.path("dealYear").asInt(),
                jsonNode.path("dealMonth").asInt(),
                jsonNode.path("dealDay").asInt()
            ),
        )
    }
}