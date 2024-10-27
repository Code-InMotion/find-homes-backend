package code_immotion.server.open_api

enum class ApiLink(
    private val baseUrl: String,
    private val abbreviation: String
) {
    APARTMENT("https://apis.data.go.kr/1613000/RTMSDataSvcApt", "Apt"), // 아파트
    ROW_HOUSE("https://apis.data.go.kr/1613000/RTMSDataSvcRH", "RH"), // 연립/다세대
    SINGLE_HOUSE("https://apis.data.go.kr/1613000/RTMSDataSvcSH", "SH"), // 단독/다가구
    OFFICETEL("https://apis.data.go.kr/1613000/RTMSDataSvcOffi", "Offi"); // 오피스텔

    fun getUrl(transactionType: TransactionType): String {
        return "$baseUrl${transactionType.value}/getTRMSDataSvc$abbreviation${transactionType.value}"
    }
}