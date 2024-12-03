package code_immotion.server.property.dto

class PropertyAggregation(
    val address: String,
    val propertyCount: Int,
    val averagePrice: Double,
    val averageDistance: Double
)