package code_immotion.server.application.api.client

import code_immotion.server.domain.property.entity.HouseType

enum class ApiLink {
    ;

    enum class Property(
        val link: String,
        val isSale: Boolean,
        val houseType: HouseType
    ) {
        SALE_APARTMENT("https://apis.data.go.kr/1613000/RTMSDataSvcAptTradeDev/getRTMSDataSvcAptSaleDev", true, HouseType.APARTMENT), // 아파트
        RENT_APARTMENT("https://apis.data.go.kr/1613000/RTMSDataSvcAptRent/getRTMSDataSvcAptRent", false, HouseType.APARTMENT), // 아파트
        SALE_ROW_HOUSE("https://apis.data.go.kr/1613000/RTMSDataSvcRHTrade/getRTMSDataSvcRHTrade", true, HouseType.VILLA), // 연립 | 다세대
        RENT_ROW_HOUSE("https://apis.data.go.kr/1613000/RTMSDataSvcRHRent/getRTMSDataSvcRHRent", false, HouseType.OFFICETEL), // 연립 | 다세대
        SALE_OFFICETEL("https://apis.data.go.kr/1613000/RTMSDataSvcOffiTrade/getRTMSDataSvcOffiTrade", true, HouseType.VILLA), // 오피스텔
        RENT_OFFICETEL("https://apis.data.go.kr/1613000/RTMSDataSvcOffiRent/getRTMSDataSvcOffiRent", false, HouseType.VILLA), // 오피스텔
    }

    enum class Subway(val link: String) {
        SUBWAY_ID("http://apis.data.go.kr/1613000/SubwayInfoService/getKwrdFndSubwaySttnList"),
        SUBWAY_SCHEDULE("https://api.odcloud.kr/api/15098251/v1/uddi:895a541f-19c4-4457-a330-99ae77b0dc4b"),
    }

    enum class Kakao(val link:String){
        GEO_LOCATION("https://dapi.kakao.com/v2/local/search/address"),
    }
}