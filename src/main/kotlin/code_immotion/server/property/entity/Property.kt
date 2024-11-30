package code_immotion.server.property.entity

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "property")
@CompoundIndex(def = "{'location': '2dsphere'}")
open class Property(
    var id: String?,
    val address: String,
    val addressNumber: String,
    val houseType: HouseType,
    val type: TradeType,
    val floor: Int,
    val price: Long,
    val rentPrint: Long?,
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

            return when {
                !amount.isMissingNode -> Sale.from(jsonNode, state, city, houseType)
                monthlyPrice.asLong() != 0L -> MonthlyRent.from(jsonNode, state, city, houseType)
                else -> DepositRent.from(jsonNode, state, city, houseType)
            }
        }
    }
}