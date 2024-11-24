package code_immotion.server.open_api

import code_immotion.server.open_api.client.ApiLink
import code_immotion.server.open_api.client.OpenApiCityCode
import code_immotion.server.open_api.client.OpenApiClient
import code_immotion.server.open_api.client.TransactionType
import code_immotion.server.property.PropertyService
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
                        val saleResponses = openApiClient.sendRequest(link, TransactionType.SALE, cityCode.code, dealMonth)
                        val rentResponses = openApiClient.sendRequest(link, TransactionType.RENT, cityCode.code, dealMonth)

                        val saleProperties = openApiClient.parseFromXml(saleResponses, cityCode.state, cityCode.city)
                        val rentProperties = openApiClient.parseFromXml(rentResponses, cityCode.state, cityCode.city)

                        saleProperties + rentProperties
                    } catch (e: Exception) {
                        logger.error { "Error fetching data for ${cityCode.city}: ${e.message}" }
                        emptyList()
                    }
                }
            }
        }.awaitAll().flatten()

        val latestProperties = newProperties.groupBy {
            it.address to it.type
        }.filter { (_, properties) ->
            properties.size > 1
        }.mapValues { (_, property) ->
            property.maxBy { it.dealDate }
        }.values.toList()

        watch.stop()
        logger.info { watch.prettyPrint() }
        logger.info { "dealMonth: $dealMonth" }
        logger.info { "properties size: ${newProperties.size}" }
        logger.info { "new properties size: ${latestProperties.size}" }

        watch.start()
        propertyService.upsertAll(latestProperties)
        watch.stop()
        println(watch.prettyPrint())
    }
}