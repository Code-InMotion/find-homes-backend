package code_immotion.server.property.dto

import code_immotion.server.property.entity.HouseType

data class PropertyResponse(
    val price: Long,
    val rentPrice: Long,
    val address: String,
    val houseType: HouseType,
    val floor: Int,
    val totalFloor: Int,
    val travelTime: Int
)