package code_immotion.server.open_api.client

import code_immotion.server.property.entity.HouseType
import code_immotion.server.property.entity.Property
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.json.XML
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder

private val logger = KotlinLogging.logger {}

@Component
class OpenApiClient {
    @Value("\${openApi.secretKey}")
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
                logger.error { xml }
                val authMsg = errorNode.path("cmmMsgHeader").path("returnAuthMsg").asText()
                throw ResponseStatusException(HttpStatus.BAD_GATEWAY, "AuthMsg: $authMsg")
            }

        val itemsNode = rootNode.path("response").path("body").path("items").path("item")

        return when {
            itemsNode.isMissingNode || (itemsNode.isArray && itemsNode.size() == 0) -> emptyList()
            itemsNode.isArray -> itemsNode.toList()
            else -> listOf(itemsNode)
        }
    }

    fun sendRequestForGeoLocation(address: String): JsonNode {
        val uri = UriComponentsBuilder
            .fromHttpUrl("https://dapi.kakao.com/v2/local/search/address")
            .queryParam("query", address)
            .build()
            .toUri()
        try {
            val xml = RestClient.create()
                .get()
                .uri(uri)
                .header("Authorization", "KakaoAK $kakaoSecretKey")
                .retrieve()
                .body(String::class.java)

            return ObjectMapper().readTree(xml).path("documents")
        } catch (e: Exception) {
            logger.error { e.printStackTrace() }
            throw Exception("kakao request error")
        }
    }

    fun parseFromXml4Data(
        items: Iterable<JsonNode>,
        state: String,
        city: String
    ): List<Property> {
        return items.map { item ->
            val houseType = when (item.path("houseType").asText()) {
//                "단독" -> HouseType.SINGLE_FAMILY
//                "다가구" -> HouseType.MULTI_FAMILY
                "연립" -> HouseType.TOWNHOUSE
                "다세대" -> HouseType.MULTI_UNIT
                else -> HouseType.APARTMENT
            }
            Property.from(item, state, city, houseType)
        }
    }
}