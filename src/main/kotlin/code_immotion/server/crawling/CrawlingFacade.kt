package code_immotion.server.crawling

import code_immotion.server.place.Place
import code_immotion.server.place.PlaceService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class CrawlingFacade(
    private val crawler: Crawler,
    private val placeService: PlaceService
) {
    fun crawlPlaces() {
        val response = crawler.crawlPlace()
        val places: List<Place> = response.map { Place.from(it) }
        placeService.saveAll(places)
        logger.info { "done" }
    }
}