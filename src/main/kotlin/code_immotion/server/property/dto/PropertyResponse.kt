package code_immotion.server.property.dto

import code_immotion.server.property.entity.HouseType
import code_immotion.server.property.entity.Property
import code_immotion.server.property.entity.TradeType
import org.springframework.data.geo.Distance

data class PropertyResponse(
    val id: String,
    val type: TradeType,
    val price: Long,
    val rentPrice: Long?,
    val address: String,
    val houseType: HouseType,
    val floor: Int,
//    val totalFloor: Int,
//    val travelTime: Int,
    val distance: Distance
) {
    companion object {
        fun from(property: Property, distance: Distance): PropertyResponse {
            return PropertyResponse(
                id = property.id!!,
                type = property.type,
                price = property.price,
                rentPrice = property.rentPrint,
                address = property.address,
                houseType = property.houseType,
                floor = property.floor,
//                totalFloor = property.totalFloor,
//                travelTime = travelTime,
                distance = distance,
            )
        }
    }
}