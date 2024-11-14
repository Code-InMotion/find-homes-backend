package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDate

open class Property(
    val address: String = "",
    val houseType: HouseType? = null,
    val buildYear: Int = 0,
    val exclusiveArea: Int = 0,
    val floor: Int = 0,
    val dealDate: LocalDate = LocalDate.now(),
    val latitude: Double? = null, // 위도
    val longitude: Double? = null, // 경도
) {
    companion object {
        fun from(jsonNode: JsonNode, state: String, city: String, houseType: HouseType) = Property(
            address = "$state $city ${jsonNode.path("umdNm").asText()} ${jsonNode.path("jibun").asText()}",
            houseType = houseType,
            buildYear = jsonNode.path("buildYear").asInt(),
            exclusiveArea = jsonNode.path("excluUseAr").asDouble().toInt(),
            floor = jsonNode.path("floor").asInt(),
            dealDate = LocalDate.of(
                jsonNode.path("dealYear").asInt(),
                jsonNode.path("dealMonth").asInt(),
                jsonNode.path("dealDay").asInt()
            )
        )
    }
}