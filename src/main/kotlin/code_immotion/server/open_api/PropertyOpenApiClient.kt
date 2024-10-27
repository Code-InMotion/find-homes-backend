package code_immotion.server.open_api

import org.json.JSONObject
import org.json.XML
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.DefaultUriBuilderFactory

@Component
class PropertyOpenApiClient(
    private val webBuilder: WebClient.Builder
) {
    @Value("\${openApi.secretKey}")
    lateinit var secretKey: String

    fun syncPropertiesWithOpenApi(): JSONObject {
        val webClient = buildBaseWebClient(ApiLink.APARTMENT, TransactionType.SALE)

        val result = webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .queryParam("serviceKey", secretKey)
                    .queryParam("LAWD_CD", "11110")
                    .queryParam("DEAL_YMD", "202409")
                    .build()
            }
            .header("Accept", "application/xml")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val json= XML.toJSONObject(result)
        println(json)
        return json
    }

    private fun buildBaseWebClient(apiLink: ApiLink, transactionType: TransactionType): WebClient {
        val baseUrl = apiLink.getUrl(transactionType)

        val factory = DefaultUriBuilderFactory(baseUrl)
            .also {
                it.encodingMode = DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY
            }

        return webBuilder
            .uriBuilderFactory(factory)
            .baseUrl(baseUrl)
            .build()
    }

}