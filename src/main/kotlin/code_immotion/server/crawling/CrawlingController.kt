package code_immotion.server.crawling

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "CRAWL API")
@RestController
@RequestMapping("crawl")
class CrawlingController(
    private val crawlingFacade: CrawlingFacade
) {
    @PostMapping
    @Hidden
    fun crawlPlace() = crawlingFacade.crawlPlaces()
}