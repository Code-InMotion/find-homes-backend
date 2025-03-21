package code_immotion.server.application.api.client

import code_immotion.server.application.handler.exception.CustomException
import code_immotion.server.application.handler.exception.ErrorCode
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.json.XML
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder

private val logger = KotlinLogging.logger {}

@Component
class ApiClient {
    @Value("\${data-go-kr.secretKey}")
    lateinit var dataSecretKey: String

    @Value("\${kakao.secretKey}")
    lateinit var kakaoSecretKey: String


    fun sendRequestToData(
        apiLink: ApiLink,
        transactionType: TransactionType,
        cityCode: Int,
        dealMonth: Int,
    ): Iterable<JsonNode> {
        val uri = UriComponentsBuilder
            .fromHttpUrl(apiLink.getUrl(transactionType))
            .queryParam("serviceKey", dataSecretKey)
            .queryParam("LAWD_CD", cityCode)
            .queryParam("DEAL_YMD", dealMonth)
            .queryParam("numOfRows", 9999)
            .build(true)
            .toUri()

        val xml = RestClient.create()
            .get()
            .uri(uri)
            .retrieve()
            .body(String::class.java)

        val json = XML.toJSONObject(xml)
        val rootNode = ObjectMapper().readTree(json.toString())

        rootNode.path("OpenAPI_ServiceResponse")
            .takeIf { !it.isMissingNode }
            ?.let { errorNode ->
                val authMsg = errorNode.path("cmmMsgHeader").path("returnAuthMsg").asText()
                val message = """
                    data: $xml
                    massage: $authMsg 
                    tradeType: $transactionType
                    cityCode: $cityCode
                    buildingType: ${apiLink.name}
                """.trimMargin()

                throw CustomException(ErrorCode.OPEN_API_SERVER, message)
            }

        val itemsNode = rootNode.path("response").path("body").path("items").path("item")

        return when {
            itemsNode.isMissingNode || (itemsNode.isArray && itemsNode.size() == 0) -> emptyList()
            itemsNode.isArray -> itemsNode.toList()
            else -> listOf(itemsNode)
        }
    }

    fun sendRequestForGeoLocation(address: String): JsonNode {
        try {
            val uri = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v2/local/search/address")
                .queryParam("query", address)
                .build()
                .toUri()

            val xml = RestClient.create()
                .get()
                .uri(uri)
                .header("Authorization", "KakaoAK $kakaoSecretKey")
                .retrieve()
                .body(String::class.java)

            return ObjectMapper().readTree(xml).path("documents")
                .takeIf { !it.isEmpty && !it.isMissingNode }
                ?: throw CustomException(ErrorCode.OPEN_API_KAKAO_SERVER)
        } catch (e: Exception) {
            logger.error { e.printStackTrace() }
            throw CustomException(ErrorCode.OPEN_API_KAKAO_SERVER, e.message)
        }
    }
}