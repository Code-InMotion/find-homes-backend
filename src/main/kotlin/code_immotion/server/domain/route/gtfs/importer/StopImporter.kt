package code_immotion.server.domain.route.gtfs.importer

import code_immotion.server.domain.route.entity.Stop
import jakarta.persistence.EntityManager
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.LocalDateTime

@Component
class StopImporter(
    private val jdbcTemplate: JdbcTemplate,
    private val entityManager: EntityManager
) : GtfsImporter {
    private var stopMap: MutableMap<String, Stop> = mutableMapOf()
    private val BATCH_SIZE = 1000

    override fun import(file: MultipartFile) {
        entityManager.flush()

        var batch = mutableListOf<Stop>()

        file.inputStream.bufferedReader().use { reader ->
            val keyMap = reader.readLine().split(",").withIndex().associate { (index, key) ->
                key.replace("\uFEFF", "") to index
            }

            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val values = line!!.split(",")
                val stop = Stop(
                    id = values[keyMap["stop_id"]!!],
                    name = values[keyMap["stop_name"]!!],
                    lat = values[keyMap["stop_lat"]!!].toDouble(),
                    lon = values[keyMap["stop_lon"]!!].toDouble()
                )
                batch.add(stop)

                if (batch.size >= BATCH_SIZE) {
                    executeBatch(batch)
                    batch = mutableListOf()
                }
            }
        }

        if (batch.isNotEmpty()) {
            executeBatch(batch)
        }

        loadStops()
    }

    override fun getFileName() = "stops.txt"
    override fun deleteAll() {
        jdbcTemplate.update("DELETE FROM stop")
    }

    fun getStopMap() = stopMap

    private fun executeBatch(batch: List<Stop>) {
        val sql = """
       INSERT INTO stop (id, name, lat, lon, updated_at)
       VALUES (?, ?, ?, ?, ?)
       """

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val stop = batch[i]
                with(ps) {
                    setString(1, stop.id)
                    setString(2, stop.name)
                    setDouble(3, stop.lat)
                    setDouble(4, stop.lon)
                    setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()))
                }
            }

            override fun getBatchSize() = batch.size
        })
    }

    private fun loadStops() {
        val sql = """
           SELECT id, name, lat, lon
           FROM stop
       """

        jdbcTemplate.query(sql) { rs, _ ->
            Stop(
                id = rs.getString("id"),
                name = rs.getString("name"),
                lat = rs.getDouble("lat"),
                lon = rs.getDouble("lon")
            )
        }.forEach { stop ->
            stopMap[stop.id] = stop
        }
    }
}
