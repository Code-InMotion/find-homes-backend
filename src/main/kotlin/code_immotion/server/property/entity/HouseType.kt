package code_immotion.server.property.entity

enum class HouseType {
    APARTMENT, // 아파트
    OFFICETEL, // 오피스텔
    // SINGLE_FAMILY,
    // MULTI_FAMILY,
    // 단독/다가구는 지번 주스 미제공
    TOWNHOUSE, // 연립
    MULTI_UNIT // 다세대
}