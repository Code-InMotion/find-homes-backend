package code_immotion.server.domain.route.gtfs.importer

import code_immotion.server.domain.route.entity.Calendar
import code_immotion.server.domain.route.repository.CalendarRepository
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class CalendarImporter(
    private val calendarRepository: CalendarRepository
) : GtfsImporter {
    private val BATCH_SIZE = 1000

    override fun import(file: MultipartFile) {
        var batch = mutableListOf<Calendar>()

        file.inputStream.bufferedReader().use { reader ->
            val keyMap = reader.readLine().split(",").withIndex().associate { (index, key) ->
                key.replace("\uFEFF", "") to index
            }

            var line: String?
            var values: List<String>
            while (reader.readLine().also { line = it } != null) {
                values = line!!.split(",")

                val startOriginDate = values[keyMap.get("start_date")!!]
                val endOriginDate = values[keyMap.get("end_date")!!]
                val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                val startDate = LocalDate.parse(startOriginDate, formatter)
                val endDate = LocalDate.parse(endOriginDate, formatter)

                val calendar = Calendar(
                    id = values[keyMap.get("service_id")!!],
                    monday = values[keyMap.get("monday")!!].toInt() == 1,
                    tuesday = values[keyMap.get("tuesday")!!].toInt() == 1,
                    wednesday = values[keyMap.get("wednesday")!!].toInt() == 1,
                    thursday = values[keyMap.get("thursday")!!].toInt() == 1,
                    friday = values[keyMap.get("friday")!!].toInt() == 1,
                    saturday = values[keyMap.get("saturday")!!].toInt() == 1,
                    sunday = values[keyMap.get("sunday")!!].toInt() == 1,
                    startDate = startDate,
                    endDate = endDate
                )

                batch.add(calendar)
            }
            if (batch.size >= BATCH_SIZE) {
                calendarRepository.saveAll(batch)
                batch = mutableListOf()
            }
        }

        if (batch.isNotEmpty()) {
            calendarRepository.saveAll(batch)
        }
    }

    override fun getFileName() = "calendar.txt"
    override fun deleteAll() {
        calendarRepository.deleteAll()
    }
}