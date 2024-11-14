package code_immotion.server.open_api

import code_immotion.server.open_api.client.ApiLink
import code_immotion.server.open_api.client.CityCode
import code_immotion.server.open_api.client.OpenApiClient
import code_immotion.server.open_api.client.TransactionType
import code_immotion.server.property.PropertyService
import code_immotion.server.property.entity.Property
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch

private val logger = KotlinLogging.logger {  }

@Component
class OpenApiFacade(
    private val openApiClient: OpenApiClient,
    private val propertyService: PropertyService
) {
    fun syncPropertiesWithOpenApi(dealMonth: Int)  {
        val list = mutableListOf<Property>()
        val watch = StopWatch()
        watch.start()
        for (link in ApiLink.entries) {
            for (cityCode in CityCode.entries) {
                val saleResponses = openApiClient.sendRequest(link, TransactionType.SALE, cityCode.code, dealMonth)
                val rentResponses = openApiClient.sendRequest(link, TransactionType.RENT, cityCode.code, dealMonth)
                val saleProperties = openApiClient.parseFromXml(saleResponses, cityCode.state, cityCode.city)
                val rentProperties = openApiClient.parseFromXml(rentResponses, cityCode.state, cityCode.city)
                list.addAll(saleProperties)
                list.addAll(rentProperties)
            }
        }
        propertyService.saveAll(list)
        watch.stop()
        logger.info { watch.prettyPrint() }
        logger.info { "size: ${list.size}" }
        logger.info { "dealMonth: $dealMonth" }
    }
}