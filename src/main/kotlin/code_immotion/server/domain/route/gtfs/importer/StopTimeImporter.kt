package code_immotion.server.domain.route.gtfs.importer

import code_immotion.server.domain.route.entity.Stop
import code_immotion.server.domain.route.entity.StopTime
import code_immotion.server.domain.route.entity.Trip
import jakarta.persistence.EntityManager
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.sql.PreparedStatement
import java.sql.Time
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Component
class StopTimeImporter(
    private val jdbcTemplate: JdbcTemplate,
    private val entityManager: EntityManager
) : GtfsImporter {
    private var tripMap: MutableMap<String, Trip> = mutableMapOf()
    private var stopMap: MutableMap<String, Stop> = mutableMapOf()
    private val BATCH_SIZE = 1000
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")


    //todo file import 시간 단축 필요 : 14분 걸림
    override fun import(file: MultipartFile) {
        entityManager.flush()

        var batch = mutableListOf<StopTime>()

        file.inputStream.bufferedReader().use { reader ->
            val keyMap = parseHeader(reader.readLine())

            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val values = line!!.split(",")
                val arrivalTime = parseTime(values[keyMap["arrival_time"]!!])
                val departureTime = parseTime(values[keyMap["departure_time"]!!])

                val stopTime = StopTime(
                    trip = tripMap[values[keyMap["trip_id"]!!]]!!,
                    stop = stopMap[values[keyMap["stop_id"]!!]]!!,
                    arrivalTime = arrivalTime,
                    departureTime = departureTime,
                    stopSequence = values[keyMap["stop_sequence"]!!].toInt(),
                    pickupType = values[keyMap["pickup_type"]!!].toInt(),
                    dropOffType = values[keyMap["drop_off_type"]!!].toInt(),
                    timePoint = values[keyMap["timepoint"]!!].toInt()
                )

                batch.add(stopTime)

                if (batch.size >= BATCH_SIZE) {
                    executeBatch(batch)
                    batch = mutableListOf()
                }
            }

            if (batch.isNotEmpty()) {
                executeBatch(batch)
            }
        }
    }

    override fun getFileName() = "stop_times.txt"
    override fun deleteAll() {
        jdbcTemplate.update(
            "DELETE FROM stop_time WHERE updated_at < ?",
            Timestamp.valueOf(LocalDateTime.now().minusHours(24))
        )
    }

    fun initStopMap(stopMap: MutableMap<String, Stop>) {
        this.stopMap = stopMap
    }

    fun initTripMap(tripMap: MutableMap<String, Trip>) {
        this.tripMap = tripMap
    }

    private fun parseTime(time: String): LocalTime {
        val parts = time.split(":")
        val hours = parts[0].toInt() % 24
        val minutes = parts[1].toInt()
        val seconds = parts[2].toInt()

        return LocalTime.of(hours, minutes, seconds)
    }

    private fun executeBatch(batch: List<StopTime>) {
        val sql = """
           INSERT INTO stop_time (
               trip_id, stop_id, arrival_time, departure_time, 
               stop_sequence, pickup_type, drop_off_type, time_point, updated_at
           )
           VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
       """

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val stopTime = batch[i]
                with(ps) {
                    setString(1, stopTime.trip.id)
                    setString(2, stopTime.stop.id)
                    setTime(3, Time.valueOf(stopTime.arrivalTime))
                    setTime(4, Time.valueOf(stopTime.departureTime))
                    setInt(5, stopTime.stopSequence)
                    setInt(6, stopTime.pickupType)
                    setInt(7, stopTime.dropOffType)
                    setInt(8, stopTime.timePoint)
                    setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()))
                }
            }

            override fun getBatchSize() = batch.size
        })
    }

    private fun parseHeader(line: String): Map<String, Int> {
        val headerMap = mutableMapOf<String, Int>()
        var fieldIndex = 0
        var startIndex = 0

        for (i in line.indices) {
            if (line[i] == ',') {
                val headerName = line.substring(startIndex, i).replace("\uFEFF", "")
                headerMap[headerName] = fieldIndex++
                startIndex = i + 1
            }
        }
        // 마지막 필드 처리
        val headerName = line.substring(startIndex).replace("\uFEFF", "")
        headerMap[headerName] = fieldIndex

        return headerMap
    }
}