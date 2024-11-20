package code_immotion.server.open_api

import code_immotion.server.open_api.client.ApiLink
import code_immotion.server.open_api.client.OpenApiCityCode
import code_immotion.server.open_api.client.OpenApiClient
import code_immotion.server.open_api.client.TransactionType
import code_immotion.server.property.PropertyService
import code_immotion.server.property.entity.Property
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
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

        val properties = ApiLink.entries.flatMap { link ->
            OpenApiCityCode.entries.map { cityCode ->
                async(Dispatchers.IO + SupervisorJob()) {
                    try {
                        // 각 도시의 매매/임대 데이터를 병렬로 가져옴
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

        propertyService.saveAll(properties)
        watch.stop()
        logger.info { watch.prettyPrint() }
        logger.info { "size: ${properties.size}" }
        logger.info { "dealMonth: $dealMonth" }
    }
}