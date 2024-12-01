package code_immotion.server.property.dto

enum class SortType(val value: String) {
    PRICE(PropertyAggregation::averagePrice.name), DISTANCE(PropertyAggregation::averageDistance.name)
}