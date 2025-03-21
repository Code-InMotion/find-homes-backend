package code_immotion.server.application.api

//import code_immotion.server.domain.property.PropertyService
import code_immotion.server.application.api.client.ApiClient
import code_immotion.server.application.api.client.ApiLink
import code_immotion.server.application.api.client.property.CityCode
import code_immotion.server.application.handler.exception.CustomException
import code_immotion.server.application.handler.exception.ErrorCode
import code_immotion.server.domain.property.entity.GeoLocation
import code_immotion.server.domain.property.entity.Property
import code_immotion.server.domain.property.entity.Rent
import code_immotion.server.domain.property.entity.Sale
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch

private val logger = KotlinLogging.logger { }

@Component
class ApiService(
    private val apiClient: ApiClient,
//    private val propertyService: PropertyService
) {
    suspend fun syncPropertiesWithOpenApi(dealMonth: Int) = coroutineScope {
        val watch = StopWatch()
        watch.start()
        val newProperties = ApiLink.Property.entries.flatMap { link ->
            CityCode.entries.map { cityCode ->
                async(Dispatchers.IO) {
                    try {
                        val response = apiClient.callPropertiesApi(link, cityCode.code, dealMonth)
                        Property.fromSale(response, cityCode.state, cityCode.city, link.houseType)
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
            }
        }.awaitAll().flatten()

        logger.info { "데이터 가져오기 완료: ${newProperties.size}개" }

        val latestProperties = selectLatestProperties(newProperties)
        logger.info { "데이터 분류 완료: ${latestProperties.size}개" }

        val propertiesWithGeo = syncGeoLocation(latestProperties)
        logger.info { "위치 정보 가져오기 완료: ${propertiesWithGeo.size}개 항목" }

        logger.info { "convert done" }
//        propertyService.upsertAll(propertiesWithGeo)
        watch.stop()
        println(watch.prettyPrint())
        logger.info { "dealMonth : $dealMonth" }
    }

    private fun selectLatestProperties(properties: List<Property>): List<Property> {
        val flatSales = properties.flatMap { property ->
            property.sales.map { sale ->
                Triple(property, sale, "SALE")
            }
        }

        val flatRents = properties.flatMap { property ->
            property.rents.map { rent ->
                val rentType = if (rent.isMonthly) "MONTHLY_RENT" else "LONG_TERM_RENT"
                Triple(property, rent, rentType)
            }
        }

        val allTransactions = flatSales + flatRents

        return allTransactions.groupBy { (property, propertyDetail, type) ->
            when (propertyDetail) {
                is Sale -> Triple(property.address + property.addressNumber, propertyDetail.floor, type)
                is Rent -> Triple(property.address + property.addressNumber, propertyDetail.floor, type)
                else -> throw CustomException(ErrorCode.BAD_REQUEST_PROPERTY)
            }
        }.mapNotNull { (_, transactions) ->
            transactions.maxByOrNull { (_, propertyDetail, _) ->
                when (propertyDetail) {
                    is Sale -> propertyDetail.dealDate
                    is Rent -> propertyDetail.dealDate
                    else -> throw CustomException(ErrorCode.BAD_REQUEST_PROPERTY)
                }
            }?.first
        }.distinct()
    }

    private suspend fun CoroutineScope.syncGeoLocation(properties: List<Property>) = properties.map { property ->
        async {
            val rootNode = apiClient.callGeoLocationApi(property.address)
            val latitude = rootNode.first().path("y").asText().toDouble()
            val longitude = rootNode.first().path("x").asText().toDouble()

            if (latitude == 0.0 || longitude == 0.0) {
                logger.error { "유효하지 않은 좌표: ${property.address}" }
                throw CustomException(ErrorCode.INVALID_LOCATION)
            }

            property.also { it.geoLocation = GeoLocation(longitude, latitude) }
        }
    }.awaitAll()
}