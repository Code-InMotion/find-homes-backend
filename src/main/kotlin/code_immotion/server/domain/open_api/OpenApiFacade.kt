package code_immotion.server.domain.open_api

import code_immotion.server.domain.open_api.client.ApiLink
import code_immotion.server.domain.open_api.client.OpenApiCityCode
import code_immotion.server.domain.open_api.client.OpenApiClient
import code_immotion.server.domain.open_api.client.TransactionType
import code_immotion.server.domain.property.PropertyService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch

private val logger = KotlinLogging.logger { }

@Component
class OpenApiFacade(
    private val openApiClient: OpenApiClient,
    private val propertyService: PropertyService
) {
    suspend fun syncPropertiesWithOpenApi(dealMonth: Int) = coroutineScope {
        val watch = StopWatch()
        watch.start()
        val newProperties = ApiLink.entries.flatMap { link ->
            OpenApiCityCode.entries.map { cityCode ->
                async(Dispatchers.IO) {
                    try {
                        val saleResponses = openApiClient.sendRequestToData(link, TransactionType.SALE, cityCode.code, dealMonth)
                        val rentResponses = openApiClient.sendRequestToData(link, TransactionType.RENT, cityCode.code, dealMonth)

                        val saleProperties = openApiClient.parseFromXml4Data(saleResponses, cityCode.state, cityCode.city, link)
                        val rentProperties = openApiClient.parseFromXml4Data(rentResponses, cityCode.state, cityCode.city, link)

                        saleProperties + rentProperties
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
            }
        }.awaitAll().flatten()

        logger.info { "fetching done" }

        val latestProperties = newProperties.groupBy {
            it.address to it.tradeType
        }.filter { (_, properties) ->
            properties.size > 1
        }.mapValues { (_, property) ->
            property.maxBy { it.dealDate }
        }.values.toList()

        val properties = latestProperties.map { property ->
            async {
                try {
                    val rootNode = openApiClient.sendRequestForGeoLocation(property.address)
                    if (!rootNode.isEmpty) {
                        property.updateLocation(rootNode.first())
                        property
                    } else null
                } catch (e: Exception) {
                    logger.error { "Error fetching data for ${property.address}: ${e.message}" }
                    null
                }
            }
        }.awaitAll()
            .filterNotNull()

        logger.info { "convert done" }
        propertyService.upsertAll(properties)
        watch.stop()
        println(watch.prettyPrint())
        logger.info { "dealMonth : $dealMonth" }
    }
}