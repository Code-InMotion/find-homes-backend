package code_immotion.server.domain.route.gtfs.importer

import code_immotion.server.domain.route.entity.Agency
import code_immotion.server.domain.route.entity.Route
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import jakarta.persistence.EntityManager
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.LocalDateTime

@Component
class RouteImporter(
    private val jdbcTemplate: JdbcTemplate,
    private val entityManager: EntityManager
) : GtfsImporter {
    private var agencyMap: MutableMap<String, Agency> = mutableMapOf()
    private var routeMap: MutableMap<String, Route> = mutableMapOf()
    private val BATCH_SIZE = 1000

    override fun import(file: MultipartFile) {
        entityManager.flush()
        var batch = mutableListOf<Route>()

        file.inputStream.bufferedReader().use { reader ->
            val csvReader = CSVReaderBuilder(reader)
                .withCSVParser(CSVParserBuilder()
                    .withSeparator(',')
                    .withQuoteChar('"')
                    .build()
                )
                .build()

            val headers = csvReader.readNext()
            val keyMap = headers.withIndex().associate { (index, key) ->
                key.replace("\uFEFF", "") to index
            }

            var values: Array<String>?
            while (csvReader.readNext().also { values = it } != null) {
                val agencyId = values!![keyMap["agency_id"]!!]
                val agency = agencyMap[agencyId] ?: throw IllegalStateException("Agency not found: $agencyId")

                val route = Route(
                    id = values!![keyMap["route_id"]!!],
                    agency = agency,
                    shortName = values!![keyMap["route_short_name"]!!],
                    longName = values!![keyMap["route_long_name"]!!],
                    type = values!![keyMap["route_type"]!!].toInt()
                )

                batch.add(route)

                if (batch.size >= BATCH_SIZE) {
                    executeBatch(batch)
                    batch = mutableListOf()
                }
            }
        }

        if (batch.isNotEmpty()) {
            executeBatch(batch)
        }

        loadRoutes(agencyMap)
    }

    override fun getFileName() = "routes.txt"
    override fun deleteAll() {
        jdbcTemplate.update("DELETE FROM route")
    }

    fun initAgencyMap(agencyMap: MutableMap<String, Agency>) {
        this.agencyMap = agencyMap
    }

    fun getRouteMap() = routeMap

    private fun executeBatch(batch: List<Route>) {
        val sql = """
       INSERT INTO route (id, agency_id, short_name, long_name, type, updated_at)
       VALUES (?, ?, ?, ?, ?, ?)
   """

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val route = batch[i]
                with(ps) {
                    setString(1, route.id)
                    setString(2, route.agency.id)
                    setString(3, route.shortName)
                    setString(4, route.longName)
                    setInt(5, route.type)
                    setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()))
                }
            }

            override fun getBatchSize() = batch.size
        })
    }

    private fun loadRoutes(agencyMap: Map<String, Agency>) {
        val sql = """
       SELECT id, agency_id, short_name, long_name, type
       FROM route
   """

        jdbcTemplate.query(sql) { rs, _ ->
            val agencyId = rs.getString("agency_id")
            val agency = agencyMap[agencyId] ?: throw IllegalStateException("Agency not found: $agencyId")

            Route(
                id = rs.getString("id"),
                agency = agency,
                shortName = rs.getString("short_name"),
                longName = rs.getString("long_name"),
                type = rs.getInt("type")
            )
        }.forEach { route ->
            routeMap[route.id] = route
        }
    }
}