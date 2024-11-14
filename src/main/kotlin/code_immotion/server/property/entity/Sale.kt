package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "property")
class Sale(
    price: Long,
    state: String,
    city: String,
    district: String,
    jibun: String,
    houseType: HouseType,
    buildYear: Int,
    exclusiveArea: Int,
    floor: Int,
    dealDate: LocalDate,
) : Property(
    price = price,
    state = state,
    city = city,
    district = district,
    jibun = jibun,
    houseType = houseType,
    buildYear = buildYear,
    exclusiveArea = exclusiveArea,
    floor = floor,
    dealDate = dealDate,
) {
    companion object {
        fun from(jsonNode: JsonNode, state: String, city: String, houseType: HouseType) = Sale(
            state = state,
            city = city,
            district = jsonNode.path("umdNm").asText(),
            jibun = jsonNode.path("jibun").asText(),
            houseType = houseType,
            buildYear = jsonNode.path("buildYear").asInt(),
            exclusiveArea = jsonNode.path("excluUseAr").asDouble().toInt(),
            floor = jsonNode.path("floor").asInt(),
            price = jsonNode.path("dealAmount").asText().replace(",", "").toLong(),
            dealDate = LocalDate.of(
                jsonNode.path("dealYear").asInt(),
                jsonNode.path("dealMonth").asInt(),
                jsonNode.path("dealDay").asInt()
            ),
        )
    }
}