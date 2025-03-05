package code_immotion.server.application.controller

import code_immotion.server.domain.route.gtfs.GtfsImportOrchestrator
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

private val logger = KotlinLogging.logger { }

@Tag(name = "GTFS 데이터 API")
@RestController
@ResponseStatus(HttpStatus.CREATED)
@RequestMapping("gtfs")
class GtfsController(private val gtfsImportOrchestrator: GtfsImportOrchestrator) {
    @PostMapping(value = ["/import"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "GTFS 데이터 최신화")
    fun importGtfsData(
        @RequestBody files: List<MultipartFile>
    ) {
        val fileMap = files.associateBy { it.originalFilename ?: "" }
        gtfsImportOrchestrator.importGtfsData(fileMap)
    }
}