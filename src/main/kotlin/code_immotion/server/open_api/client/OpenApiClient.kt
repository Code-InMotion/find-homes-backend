package code_immotion.server.open_api.client

import code_immotion.server.property.entity.BuildingType
import code_immotion.server.property.entity.Property
import code_immotion.server.property.entity.Rent
import code_immotion.server.property.entity.Sale
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
    lateinit var secretKey: String

    fun sendRequest(
        apiLink: ApiLink,
        transactionType: TransactionType,
        cityCode: Int,
        dealMonth: Int,
    ): Iterable<JsonNode> {
        val uri = UriComponentsBuilder
            .fromHttpUrl(apiLink.getUrl(transactionType))
            .queryParam("serviceKey", secretKey)
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

    fun parseFromXml(
        items: Iterable<JsonNode>,
        state: String,
        city: String
    ): List<Property> {
        return items.map { item ->
            val houseType = when (item.path("houseType").asText()) {
                "단독" -> BuildingType.SINGLE_FAMILY
                "다가구" -> BuildingType.MULTI_FAMILY
                "연립" -> BuildingType.TOWNHOUSE
                "다세대" -> BuildingType.MULTI_UNIT
                else -> BuildingType.APARTMENT
            }
            val amount = item.path("dealAmount")
            if (amount.isMissingNode) Rent.from(item, state, city, houseType)
            else Sale.from(item, state, city, houseType)
        }
    }
}