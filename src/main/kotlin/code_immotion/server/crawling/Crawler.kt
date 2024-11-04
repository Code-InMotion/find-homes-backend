package code_immotion.server.crawling

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class Crawler {

    fun crawlPlace() {
        val response = RestClient.create()
            .get()
            .uri("https://place.map.kakao.com/m/main/v/501269522")
            .retrieve()
            .body(String::class.java)

        val rootNode = ObjectMapper().readTree(response)
    }
}