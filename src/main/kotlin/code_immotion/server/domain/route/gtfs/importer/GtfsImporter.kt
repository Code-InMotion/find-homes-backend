package code_immotion.server.domain.route.gtfs.importer

import org.springframework.web.multipart.MultipartFile

sealed interface GtfsImporter {
    fun import(file: MultipartFile)
    fun getFileName(): String
    fun deleteAll()
}