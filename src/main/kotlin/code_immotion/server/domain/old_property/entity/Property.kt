package code_immotion.server.domain.old_property.entity

import code_immotion.server.application.handler.exception.CustomException
import code_immotion.server.application.handler.exception.ErrorCode
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "property")
@CompoundIndex(def = "{'location': '2dsphere'}")
open class Property(
    var id: String?,
    val buildingName: String,
    val address: String,
    val addressNumber: String,
    val houseType: HouseType,
    val tradeType: TradeType,
    val floor: Int,
    val price: Long,
    val rentPrice: Long?,
    val dealDate: LocalDate,
    val buildYear: Int,
    val exclusiveArea: Int,
) {
    var location: Point? = null

    fun updateLocation(rootNode: JsonNode) {
        if (rootNode.isEmpty) return
        val latitude = rootNode.path("y").asText().toDouble()
        val longitude = rootNode.path("x").asText().toDouble()
        this.location = Point(longitude, latitude)
    }

    companion object {
        fun from(jsonNode: JsonNode, state: String, city: String, houseType: HouseType): Property {
            val amount = jsonNode.path("dealAmount")
            val monthlyPrice = jsonNode.path("monthlyRent")
            val buildingName = when {
                jsonNode.hasNonNull("offiNm") -> jsonNode.path("offiNm").asText()
                jsonNode.hasNonNull("aptNm") -> jsonNode.path("aptNm").asText()
                jsonNode.hasNonNull("mhouseNm") -> jsonNode.path("mhouseNm").asText()
                else -> throw CustomException(ErrorCode.BAD_REQUEST)
            }

            return when {
                !amount.isMissingNode -> Sale.from(jsonNode, state, city, houseType, buildingName)
                monthlyPrice.asLong() != 0L -> MonthlyRent.from(jsonNode, state, city, houseType,buildingName)
                else -> DepositRent.from(jsonNode, state, city, houseType,buildingName)
            }
        }
    }
}