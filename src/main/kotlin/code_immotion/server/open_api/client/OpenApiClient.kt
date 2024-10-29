package code_immotion.server.open_api.client

import code_immotion.server.property.entity.BuildingType
import code_immotion.server.property.entity.Sale
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.json.XML
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.DefaultUriBuilderFactory
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

@Component
class OpenApiClient(
    private val webBuilder: WebClient.Builder
) {
    @Value("\${openApi.secretKey}")
    lateinit var secretKey: String

    fun sendRequest(
        apiLink: ApiLink,
        transactionType: TransactionType,
        cityCode: Int,
        dealMonth: Int
    ): String? {
        val webClient = buildBaseWebClient(apiLink, transactionType)

        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .queryParam("serviceKey", secretKey)
                    .queryParam("LAWD_CD", cityCode)
                    .queryParam("DEAL_YMD", dealMonth)
                    .queryParam("numOfRows", 9999)
                    .build()
            }
            .header("Accept", "application/xml")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }

    fun parseSalesFromXml(xmlData: String): List<Sale> {
        val json = XML.toJSONObject(xmlData)
        val rootNode = ObjectMapper().readTree(json.toString())

        val errorNode = rootNode.path("OpenAPI_ServiceResponse")
        if (!errorNode.isMissingNode) {
            val authMsg = errorNode.path("cmmMsgHeader").path("returnAuthMsg")
            throw ResponseStatusException(HttpStatus.BAD_GATEWAY, "AuthMsg : ${authMsg.asText()}")
        }

        // 정상 데이터 파싱
        val itemsNode = rootNode.path("response").path("body").path("items").path("item")
        return itemsNode.map { itemNode ->
            Sale(
                amount = itemNode.path("dealAmount").asText().replace(",", "").toInt(),
                state = "서울특별시",
                city = itemNode.path("umdNm").asText(),
                buildingType = BuildingType.APARTMENT,
                buildYear = itemNode.path("buildYear").asInt(),
                exclusiveArea = itemNode.path("excluUseAr").asDouble().toInt(),
                floor = itemNode.path("floor").asInt(),
                dealDate = LocalDate.of(
                    itemNode.path("dealYear").asInt(),
                    itemNode.path("dealMonth").asInt(),
                    itemNode.path("dealDay").asInt()
                )
            )
        }
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