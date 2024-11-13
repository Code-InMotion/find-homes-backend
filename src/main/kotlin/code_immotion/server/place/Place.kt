package code_immotion.server.place

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "place")
class Place(
    val cid: Long,
    val name: String,
    val address: String,
    val pointX: Int,
    val pointY: Int,
) {
    companion object {
        fun from(node: JsonNode): Place {
            val basicInfo = node.path("basicInfo")
            val address = basicInfo.path("address")

            return Place(
                cid = basicInfo.path("cid").asLong(),
                name = basicInfo.path("placenamefull").asText(),
                address = "${address.path("region").path("fullname").asText()} ${address.path("addrbunho").asText()}",
                pointX = basicInfo.path("wpointx").asInt(),
                pointY = basicInfo.path("wpointy").asInt(),
            )
        }
    }
}