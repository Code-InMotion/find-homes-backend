package code_immotion.server.domain.route.gtfs

import code_immotion.server.domain.route.entity.Agency
import code_immotion.server.domain.route.entity.Route
import code_immotion.server.domain.route.entity.Stop
import code_immotion.server.domain.route.entity.Trip
import code_immotion.server.domain.route.gtfs.importer.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StopWatch
import org.springframework.web.multipart.MultipartFile

private val logger = KotlinLogging.logger { }

@Service
class GtfsImportOrchestrator(
    private val importers: List<GtfsImporter>
) {

    @Transactional
    fun importGtfsData(files: Map<String, MultipartFile>) {
        val orderedImporters = importers.sortedBy {
            when (it) {
                is AgencyImporter -> 1
                is StopImporter -> 2
                is RouteImporter -> 3
                is TripImporter -> 4
                is StopTimeImporter -> 5
                is CalendarImporter -> 6
            }
        }

        for (importer in orderedImporters.reversed()) {
            logger.info { "Deleting ${importer.getFileName()}" }
            importer.deleteAll()
        }

        var agencyMap: MutableMap<String, Agency> = mutableMapOf()
        var routeMap: MutableMap<String, Route> = mutableMapOf()
        var tripMap: MutableMap<String, Trip> = mutableMapOf()
        var stopMap: MutableMap<String, Stop> = mutableMapOf()

        val watcher = StopWatch()
        watcher.start()

        for (importer in orderedImporters) {
            when (importer) {
                is RouteImporter -> importer.initAgencyMap(agencyMap)
                is TripImporter -> importer.initRouteMap(routeMap)
                is StopTimeImporter -> {
                    importer.initStopMap(stopMap)
                    importer.initTripMap(tripMap)
                }

                else -> Unit
            }
            files[importer.getFileName()]?.let { file ->
                logger.info { "Importing ${importer.getFileName()}" }
                importer.import(file)

                when (importer) {
                    is AgencyImporter -> agencyMap = importer.getAgencyMap()
                    is RouteImporter -> routeMap = importer.getRouteMap()
                    is TripImporter -> tripMap = importer.getTripMap()
                    is StopImporter -> stopMap = importer.getStopMap()
                    else -> Unit
                }

                logger.info { "Successfully imported ${importer.getFileName()}" }
            }
        }

        watcher.stop()
        println(watcher.prettyPrint())
    }
}