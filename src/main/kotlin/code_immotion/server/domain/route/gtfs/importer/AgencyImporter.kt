package code_immotion.server.domain.route.gtfs.importer

import code_immotion.server.domain.route.entity.Agency
import code_immotion.server.domain.route.repository.AgencyRepository
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class AgencyImporter(
    private val agencyRepository: AgencyRepository
) : GtfsImporter {
    private val agencyMap: MutableMap<String, Agency> = mutableMapOf()
    private val BATCH_SIZE = 1000

    override fun import(file: MultipartFile) {
        var batch = mutableListOf<Agency>()

        file.inputStream.bufferedReader().use { reader ->
            val keyMap = reader.readLine().split(",").withIndex().associate { (index, key) ->
                key.replace("\uFEFF", "") to index
            }

            var line: String?
            var values: List<String>
            while (reader.readLine().also { line = it } != null) {
                values = line!!.split(",")
                val agency = Agency(
                    id = values[keyMap.get("agency_id")!!],
                    name = values[keyMap.get("agency_name")!!],
                    url = values[keyMap.get("agency_url")!!],
                    timezone = values[keyMap.get("agency_timezone")!!]
                )
                batch.add(agency)
            }

            if (batch.size >= BATCH_SIZE) {
                for (agency in agencyRepository.saveAll(batch)) {
                    agencyMap[agency.id] = agency
                }
                batch = mutableListOf()
            }
        }

        if (batch.isNotEmpty()) {
            for (agency in agencyRepository.saveAll(batch)) {
                agencyMap[agency.id] = agency
            }
        }
    }

    override fun getFileName() = "agency.txt"
    override fun deleteAll() {
        agencyRepository.deleteAll()
    }

    fun getAgencyMap() = agencyMap
}