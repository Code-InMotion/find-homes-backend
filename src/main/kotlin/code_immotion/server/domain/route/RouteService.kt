package code_immotion.server.domain.route

import code_immotion.server.domain.open_api.client.OpenApiClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate

private val logger = KotlinLogging.logger { }

@Service
class RouteService(private val openApiClient: OpenApiClient) {
    @Value("\${otp.url}")
    lateinit var otpUrl: String

    private val mapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    private val restClient by lazy {
        RestClient.builder()
            .baseUrl(otpUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8")
            .build()
    }

    fun findRouteMinTime(start: String, arrive: String): Int {
        val response = restClient.get()
            .uri {
                it.queryParam("fromPlace", start)
                    .queryParam("toPlace", arrive)
                    .queryParam("arriveBy", false)
                    .queryParam("mode", "TRANSIT,WALK")
                    .queryParam("date", LocalDate.now())
                    .queryParam("time", URLEncoder.encode("7:30am", StandardCharsets.UTF_8))
                    .build()
            }
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                throw RuntimeException("data error")
            }
            .onStatus(HttpStatusCode::is5xxServerError) { _, response ->
                throw RuntimeException("server error")
            }
            .body(String::class.java)

        val jsonNode = mapper.readTree(response)
        val times = jsonNode.path("plan")
            .path("itineraries")
            .mapNotNull {
                it.path("duration")
                    .takeUnless { it.isMissingNode }?.asInt()
            }

        return times.minOrNull() ?: throw RuntimeException("No routes found")
    }
}