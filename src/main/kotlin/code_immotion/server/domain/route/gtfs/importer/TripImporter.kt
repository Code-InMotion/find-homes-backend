package code_immotion.server.domain.route.gtfs.importer

import code_immotion.server.domain.route.entity.Route
import code_immotion.server.domain.route.entity.Trip
import jakarta.persistence.EntityManager
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.LocalDateTime

@Component
class TripImporter(
    private val jdbcTemplate: JdbcTemplate,
    private val entityManager: EntityManager
) : GtfsImporter {
    private var routeMap: MutableMap<String, Route> = mutableMapOf()
    private var tripMap: MutableMap<String, Trip> = mutableMapOf()
    private val BATCH_SIZE = 1000

    override fun import(file: MultipartFile) {
        entityManager.flush()

        var batch = mutableListOf<Trip>()

        file.inputStream.bufferedReader().use { reader ->
            val keyMap = reader.readLine().split(",").withIndex().associate { (index, key) ->
                key.replace("\uFEFF", "") to index
            }

            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val values = line!!.split(",")
                val routeId = values[keyMap["route_id"]!!]
                val route = routeMap[routeId] ?: throw IllegalStateException("Route not found: $routeId")

                val trip = Trip(
                    id = values[keyMap["trip_id"]!!],
                    route = route,
                    serviceId = values[keyMap["service_id"]!!]
                )

                batch.add(trip)

                if (batch.size >= BATCH_SIZE) {
                    executeBatch(batch)
                    batch = mutableListOf()
                }
            }
        }

        if (batch.isNotEmpty()) {
            executeBatch(batch)
        }

        loadTrips(routeMap)
    }

    override fun getFileName() = "trips.txt"
    override fun deleteAll() {
        jdbcTemplate.update("DELETE FROM trip")
    }

    fun initRouteMap(routeMap: MutableMap<String, Route>) {
        this.routeMap = routeMap
    }

    fun getTripMap() = tripMap

    private fun executeBatch(batch: List<Trip>) {
        val sql = """
            INSERT INTO trip (id, route_id, service_id, updated_at)
            VALUES (?, ?, ?, ?)
        """

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val trip = batch[i]
                with(ps) {
                    setString(1, trip.id)
                    setString(2, trip.route.id)  // route_id는 외래키
                    setString(3, trip.serviceId)
                    setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()))
                }
            }

            override fun getBatchSize() = batch.size
        })
    }

    private fun loadTrips(routeMap: Map<String, Route>) {
        val sql = """
            SELECT id, route_id, service_id
            FROM trip
        """

        jdbcTemplate.query(sql) { rs, _ ->
            val routeId = rs.getString("route_id")
            val route = routeMap[routeId] ?: throw IllegalStateException("Route not found: $routeId")

            Trip(
                id = rs.getString("id"),
                route = route,  // 이미 존재하는 Route 객체 사용
                serviceId = rs.getString("service_id")
            )
        }.forEach { trip ->
            tripMap[trip.id] = trip
        }
    }
}