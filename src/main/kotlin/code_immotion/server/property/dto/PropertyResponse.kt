package code_immotion.server.property.dto

import code_immotion.server.property.entity.HouseType
import code_immotion.server.property.entity.Property
import code_immotion.server.property.entity.TradeType
import org.springframework.data.geo.Distance

data class PropertyResponse(
    val id: String,
    val tradeType: TradeType,
    val price: Long,
    val rentPrice: Long?,
    val address: String,
    val addressNumber: String,
    val houseType: HouseType,
    val floor: Int,
    val travelTime: Int,
    val distance: Distance
) {
    companion object {
        fun from(property: Property, distance: Distance): PropertyResponse {
            return PropertyResponse(
                id = property.id!!,
                tradeType = property.tradeType,
                price = property.price,
                rentPrice = property.rentPrice,
                address = property.address,
                addressNumber = property.addressNumber,
                houseType = property.houseType,
                floor = property.floor,
                travelTime = 60, //todo 이동 시간 수정 필요
                distance = distance,
            )
        }
    }
}