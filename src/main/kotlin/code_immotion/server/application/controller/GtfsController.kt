package code_immotion.server.application.controller

import code_immotion.server.application.gtfs.GtfsService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger { }

@Tag(name = "GTFS 데이터 API")
@RestController
@ResponseStatus(HttpStatus.CREATED)
@RequestMapping("gtfs")
class GtfsController(private val gtfsService: GtfsService) {

    @PostMapping("", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createSubwayGtfs(
        @RequestPart stationMasterFile: MultipartFile,
        @RequestPart timetableFile: MultipartFile
    ): ByteArray {
        if (stationMasterFile.isEmpty || timetableFile.isEmpty) {
            logger.error { "파일이 비어 있습니다." }
        }

        // 파일 확장자 검증
        if (!stationMasterFile.originalFilename?.lowercase()?.endsWith(".json")!!) {
            logger.error { "역사 마스터 파일은 JSON 형식이어야 합니다." }
        }

        if (!timetableFile.originalFilename?.lowercase()?.endsWith(".xlsx")!!) {
            logger.error { "시간표 파일은 XLSX 형식이어야 합니다." }
        }

        // 파일 내용 읽기
        val stationMasterJson = String(stationMasterFile.bytes, StandardCharsets.UTF_8)

        // GTFS ZIP 파일 생성
        val gtfsZipData = gtfsService.generateGtfsZip(stationMasterJson, timetableFile.inputStream)

        // 다운로드 응답 생성
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val timestamp = LocalDateTime.now().format(formatter)
        val filename = "gtfs_${timestamp}.zip"

        return gtfsZipData
    }
}