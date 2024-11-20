package code_immotion.server.crawling

enum class CityCode(val code: Int, val address: String) {
    SEOUL_GANGNAM(1168000000, "서울시 강남구"),
    SEOUL_GANGDONG(1174000000, "서울시 강동구"),
    SEOUL_GANGBUK(1130500000, "서울시 강북구"),
    SEOUL_GANGSEO(1150000000, "서울시 강서구"),
    SEOUL_GWANAK(1162000000, "서울시 관악구"),
    SEOUL_GWANGJIN(1121500000, "서울시 광진구"),
    SEOUL_GURO(1153000000, "서울시 구로구"),
    SEOUL_GEUMCHEON(1154500000, "서울시 금천구"),
    SEOUL_NOWON(1135000000, "서울시 노원구"),
    SEOUL_DOBONG(1132000000, "서울시 도봉구"),
    SEOUL_DONGDAEMUN(1123000000, "서울시 동대문구"),
    SEOUL_DONGJAK(1159000000, "서울시 동작구"),
    SEOUL_MAPO(1144000000, "서울시 마포구"),
    SEOUL_SEODAEMUN(1141000000, "서울시 서대문구"),
    SEOUL_SEOCHO(1165000000, "서울시 서초구"),
    SEOUL_SEONGDONG(1120000000, "서울시 성동구"),
    SEOUL_SEONGBUK(1129000000, "서울시 성북구"),
    SEOUL_SONGPA(1171000000, "서울시 송파구"),
    SEOUL_YANGCHEON(1147000000, "서울시 양천구"),
    SEOUL_YEONGDEUNGPO(1156000000, "서울시 영등포구"),
    SEOUL_YONGSAN(1117000000, "서울시 용산구"),
    SEOUL_EUNPYEONG(1138000000, "서울시 은평구"),
    SEOUL_JONGNO(1111000000, "서울시 종로구"),
    SEOUL_JUNG(1114000000, "서울시 중구"),
    SEOUL_JUNGNANG(1126000000, "서울시 중랑구");
}