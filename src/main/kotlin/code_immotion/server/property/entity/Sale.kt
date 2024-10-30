package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "property")
class Sale(
    val amount: Int,
    state: String,
    city: String,
    district: String,
    jibun: String,
    buildingType: BuildingType,
    buildYear: Int,
    exclusiveArea: Int,
    floor: Int,
    dealDate: LocalDate,
) : Property(
    state = state,
    city = city,
    district = district,
    jibun = jibun,
    buildingType = buildingType,
    buildYear = buildYear,
    exclusiveArea = exclusiveArea,
    floor = floor,
    dealDate = dealDate,
) {
    companion object {
        fun from(jsonNode: JsonNode, state: String, city: String, buildingType: BuildingType) = Sale(
            state = state,
            city = city,
            district = jsonNode.path("umdNm").asText(),
            jibun = jsonNode.path("jibun").asText(),
            buildingType = buildingType,
            buildYear = jsonNode.path("buildYear").asInt(),
            exclusiveArea = jsonNode.path("excluUseAr").asDouble().toInt(),
            floor = jsonNode.path("floor").asInt(),
            amount = jsonNode.path("dealAmount").asText().replace(",", "").toInt(),
            dealDate = LocalDate.of(
                jsonNode.path("dealYear").asInt(),
                jsonNode.path("dealMonth").asInt(),
                jsonNode.path("dealDay").asInt()
            ),
        )
    }
}