package code_immotion.server.crawling

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger { }

@Component
class Crawler {

    fun crawlPlace(): List<JsonNode> {
        val webClient = buildWebClient("https://place.map.kakao.com/m/main/v")

        return (Flux.range(0, 1000)
            .window(5) // 5개씩 묶어서 처리
            .flatMap { window ->
                window.flatMap({ index ->
                    webClient.get()
                        .uri { it.path("/$index").build() }
                        .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                        )
                        .retrieve()
                        .bodyToMono(String::class.java)
                        .filter {
                            println(index)
                            val rootNode = ObjectMapper().readTree(it)
                            val isExist = rootNode.path("isExist")
                            val address = rootNode.path("basic").path("region").path("fullname").asText()
                            !isExist.isMissingNode && isExist.asBoolean() && (address.startsWith("경기") || address.startsWith("서울"))
                        }
                        .map { ObjectMapper().readTree(it) }
                        .onErrorResume {
                            println("Error at index $index: ${it.message}")
                            Mono.empty()
                        }
                }, 5)
            }
            .filter { it != null }
            .collectList()
            .block() ?: emptyList())
    }

    private fun buildWebClient(url: String): WebClient {
        val provider = ConnectionProvider.builder("custom")
            .maxConnections(500)
            .pendingAcquireMaxCount(1000)
            .pendingAcquireTimeout(Duration.ofMillis(10000))
            .build()

        val httpClient = HttpClient.create(provider)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .responseTimeout(Duration.ofMillis(5000))
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                conn.addHandlerLast(WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS))
            }
            .option(ChannelOption.SO_KEEPALIVE, true)

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(url)
            .codecs { it.defaultCodecs().maxInMemorySize(2 * 1024 * 1024) }
            .build()
    }
}