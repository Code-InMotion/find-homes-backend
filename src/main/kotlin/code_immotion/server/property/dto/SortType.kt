package code_immotion.server.property.dto

enum class SortType(val value: String) {
    PRICE(PropertyAggregation.Data::averagePrice.name), DISTANCE(PropertyAggregation.Data::averageDistance.name)
}