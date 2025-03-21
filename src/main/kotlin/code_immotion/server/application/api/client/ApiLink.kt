package code_immotion.server.application.api.client

import code_immotion.server.application.api.client.property.TransactionType

enum class ApiLink(
    private val baseUrl: String,
    private val abbreviation: String
) {
    APARTMENT("https://apis.data.go.kr/1613000/RTMSDataSvcApt", "Apt"), // 아파트
    ROW_HOUSE("https://apis.data.go.kr/1613000/RTMSDataSvcRH", "RH"), // 연립/다세대
    //    SINGLE_HOUSE("https://apis.data.go.kr/1613000/RTMSDataSvcSH", "SH"), // 단독/다가구
    OFFICETEL("https://apis.data.go.kr/1613000/RTMSDataSvcOffi", "Offi"), // 오피스텔

    SUBWAY_ID("http://apis.data.go.kr/1613000/SubwayInfoService", "getKwrdFndSubwaySttnList"),
    SUBWAY_SCHEDULE("http://apis.data.go.kr/1613000/SubwayInfoService", "getSubwaySttnAcctoSchdulList")
    ;

    fun getUrl(transactionType: TransactionType): String {
        return if (this == APARTMENT && transactionType == TransactionType.SALE) "$baseUrl${transactionType.value}Dev/getRTMSDataSvc$abbreviation${transactionType.value}Dev"
        else "$baseUrl${transactionType.value}/getRTMSDataSvc$abbreviation${transactionType.value}"
    }
}