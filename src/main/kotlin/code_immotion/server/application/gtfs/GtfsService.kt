package code_immotion.server.application.gtfs

import code_immotion.server.application.api.client.ApiClient
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Service
class GtfsService(private val apiClient: ApiClient) {
    // 역사 정보 데이터 클래스
    data class StationInfo(
        val bldn_id: String = "",     // 기본값 추가
        val route: String = "",       // 기본값 추가
        val lot: String = "",         // 기본값 추가
        val bldn_nm: String = "",     // 기본값 추가
        val lat: String = ""          // 기본값 추가
    )

    // 역사 마스터 응답 데이터 클래스
    data class StationMasterResponse @JsonCreator constructor(
        @JsonProperty("DESCRIPTION") val DESCRIPTION: Map<String, String> = emptyMap(),
        @JsonProperty("DATA") val DATA: List<StationInfo> = emptyList()
    )

    /**
     * 역사 마스터 JSON과 시간표 엑셀을 이용하여 GTFS ZIP 파일을 생성합니다.
     * @param stationMasterJson 역사 마스터 JSON 파일 내용
     * @param timetableExcelInputStream 시간표 엑셀 파일 입력 스트림
     * @return GTFS 파일들이 담긴 ZIP 파일 바이트 배열
     */
    fun generateGtfsZip(stationMasterJson: String, timetableExcelInputStream: InputStream): ByteArray {
        // JSON 파싱
        val objectMapper = ObjectMapper()
        val stationMaster = objectMapper.readValue<StationMasterResponse>(stationMasterJson)

        // 노선별 역 정보 매핑
        val routeStations = stationMaster.DATA.groupBy { it.route }

        // 노선 ID 매핑 생성
        val routeToId = mutableMapOf<String, String>()
        routeStations.keys.forEachIndexed { index, route ->
            routeToId[route] = "R${index + 1}"
        }

        // GTFS 파일 생성
        val stopsContent = generateStops(stationMaster.DATA)
        val routesContent = generateRoutes(routeToId)
        val calendarContent = generateCalendar()

        // trips.txt 및 stop_times.txt 파일 생성
        val workbook = WorkbookFactory.create(timetableExcelInputStream)
        val tripsAndStopTimes = generateTripsAndStopTimes(workbook, routeStations, routeToId)
        workbook.close()

        // ZIP 파일로 압축
        return createZipFile(
            mapOf(
                "agency.txt" to "agency_id,agency_name,agency_url,agency_timezone\nA1,KTDB,http://www.ktdb.go.kr/,Asia/Seoul",
                "stops.txt" to stopsContent,
                "routes.txt" to routesContent,
                "calendar.txt" to calendarContent,
                "trips.txt" to tripsAndStopTimes.first,
                "stop_times.txt" to tripsAndStopTimes.second
            )
        )
    }

    /**
     * 역사 정보를 이용하여 stops.txt 파일 내용을 생성합니다.
     */
    private fun generateStops(stations: List<StationInfo>): String {
        return buildString {
            // 헤더 추가
            appendLine("stop_id,stop_name,stop_lat,stop_lon,location_type,parent_station")

            // 각 역사에 대한 정보 추가
            stations.forEach { station ->
                appendLine("${station.bldn_id},${station.bldn_nm},${station.lat},${station.lot},0,")
            }
        }
    }

    /**
     * 노선 정보를 이용하여 routes.txt 파일 내용을 생성합니다.
     */
    private fun generateRoutes(routeToId: Map<String, String>): String {
        return buildString {
            // 헤더 추가
            appendLine("route_id,agency_id,route_short_name,route_long_name,route_type,route_color,route_text_color")

            // 각 노선에 대한 정보 추가
            routeToId.forEach { (routeName, routeId) ->
                val routeColor = getRouteColor(routeName)
                appendLine("$routeId,A1,$routeName,$routeName,1,$routeColor,FFFFFF")
            }
        }
    }

    /**
     * calendar.txt 파일 내용을 생성합니다.
     */
    private fun generateCalendar(): String {
        return buildString {
            // 헤더 추가
            appendLine("service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date")

            // 평일/주말 운행 정보 추가
            appendLine("WEEKDAY,1,1,1,1,1,0,0,20250101,20251231")
            appendLine("WEEKEND,0,0,0,0,0,1,1,20250101,20251231")
        }
    }

    /**
     * trips.txt 및 stop_times.txt 파일 내용을 생성합니다.
     */
    private fun generateTripsAndStopTimes(
        workbook: org.apache.poi.ss.usermodel.Workbook,
        routeStations: Map<String, List<StationInfo>>,
        routeToId: Map<String, String>
    ): Pair<String, String> {
        val trips = StringBuilder().apply {
            appendLine("route_id,service_id,trip_id,trip_headsign,direction_id,shape_id")
        }

        val stopTimes = StringBuilder().apply {
            appendLine("trip_id,arrival_time,departure_time,stop_id,stop_sequence,pickup_type,drop_off_type")
        }

        // 각 시트(노선)별로 처리
        for (sheetIndex in 0 until workbook.numberOfSheets) {
            val sheet = workbook.getSheetAt(sheetIndex)
            val sheetName = sheet.sheetName

            // 해당 노선 찾기
            val routeName = extractRouteName(sheetName)
            val routeId = routeToId[routeName] ?: continue
            val stationList = routeStations[routeName] ?: continue

            // 상행/하행별 trip 생성
            for (directionId in 0..1) {
                val directionName = if (directionId == 0) "상행" else "하행"
                val stations = if (directionId == 0) stationList else stationList.reversed()
                val tripHeadsign = "$routeName ${stations.last().bldn_nm}행"

                // 각 시간대별 trip 생성
                for (hour in 5..23) {
                    val tripId = "${routeId}_${directionId}_${hour}"
                    trips.appendLine("$routeId,WEEKDAY,$tripId,$tripHeadsign,$directionId,")

                    // 가상의 정차 시간 생성 (실제로는 엑셀에서 해당 시간대 데이터를 추출해야 함)
                    stations.forEachIndexed { stationIndex, station ->
                        // 각 역별 3분 간격으로 도착 시간 계산
                        val minutes = stationIndex * 3
                        val arrivalTime = formatTime(hour, minutes)
                        val departureTime = formatTime(hour, minutes + 1)

                        stopTimes.appendLine("$tripId,$arrivalTime,$departureTime,${station.bldn_id},$stationIndex,0,0")
                    }
                }
            }
        }

        return Pair(trips.toString(), stopTimes.toString())
    }

    /**
     * 시트 이름에서 노선 이름을 추출합니다.
     */
    private fun extractRouteName(sheetName: String): String {
        // 엑셀 시트명에서 노선 이름 추출
        return when {
            sheetName.contains("1호선") -> "1호선"
            sheetName.contains("2호선") -> "2호선"
            sheetName.contains("3호선") -> "3호선"
            sheetName.contains("4호선") -> "4호선"
            sheetName.contains("5호선") -> "5호선"
            sheetName.contains("6호선") -> "6호선"
            sheetName.contains("7호선") -> "7호선"
            sheetName.contains("8호선") -> "8호선"
            sheetName.contains("9호선") -> "9호선"
            else -> sheetName
        }
    }

    /**
     * 시간 포맷을 지정된 형식으로 변환합니다.
     */
    private fun formatTime(hour: Int, minutes: Int): String {
        val time = LocalTime.of(hour, minutes % 60)
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }

    /**
     * 노선별 색상 코드를 반환합니다.
     */
    private fun getRouteColor(routeName: String): String {
        // 서울 지하철 노선별 고유 색상
        return when {
            routeName.startsWith("1호선") -> "0052A4" // 파랑
            routeName.startsWith("2호선") -> "00A84D" // 초록
            routeName.startsWith("3호선") -> "EF7C1C" // 주황
            routeName.startsWith("4호선") -> "00A4E3" // 하늘
            routeName.startsWith("5호선") -> "996CAC" // 보라
            routeName.startsWith("6호선") -> "CD7C2F" // 황토
            routeName.startsWith("7호선") -> "747F00" // 갈녹
            routeName.startsWith("8호선") -> "E6186C" // 분홍
            routeName.startsWith("9호선") -> "BDB092" // 금황
            routeName.contains("분당선") -> "FFA100" // 노랑
            routeName.contains("신분당선") -> "D4003B" // 빨강
            routeName.contains("공항철도") -> "0090D2" // 하늘
            routeName.contains("경의중앙선") -> "77C4A3" // 연두
            routeName.contains("경춘선") -> "0C8E72" // 청록
            routeName.contains("수인선") -> "F5A200" // 주황
            routeName.contains("경강선") -> "003DA5" // 파랑
            routeName.contains("우이신설선") -> "B7C452" // 연두
            routeName.contains("서해선") -> "81A914" // 연두
            routeName.contains("김포골드라인") -> "A17800" // 황금
            else -> "AAAAAA" // 기본 회색
        }
    }

    /**
     * 파일 컨텐츠를 ZIP 파일로 압축합니다.
     */
    private fun createZipFile(fileContents: Map<String, String>): ByteArray {
        val byteStream = java.io.ByteArrayOutputStream()
        ZipOutputStream(byteStream).use { zipOut ->
            fileContents.forEach { (fileName, content) ->
                val zipEntry = ZipEntry(fileName)
                zipOut.putNextEntry(zipEntry)
                zipOut.write(content.toByteArray(Charsets.UTF_8))
                zipOut.closeEntry()
            }
        }
        return byteStream.toByteArray()
    }
}