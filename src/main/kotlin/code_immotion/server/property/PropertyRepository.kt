package code_immotion.server.property

import code_immotion.server.property.dto.PropertyCondition
import code_immotion.server.property.dto.PropertyResponse
import code_immotion.server.property.entity.MonthlyRent
import code_immotion.server.property.entity.Property
import code_immotion.server.property.entity.TradeType
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeospatialIndex
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.NearQuery
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class PropertyRepository(private val mongoTemplate: MongoTemplate) {
    fun saveAll(properties: List<Property>) {
        mongoTemplate.insertAll(properties)
    }

    fun upsertAll(properties: List<Property>) {
        properties.forEach { property ->
            val query = Query(
                Criteria().andOperator(
                    Criteria.where(Property::address.name).`is`(property.address),
                    Criteria.where(Property::tradeType.name).`is`(property.tradeType.name)
                )
            )

            val update = Update()
                .setOnInsert(Property::price.name, property.price)
                .setOnInsert(Property::floor.name, property.floor)
                .setOnInsert(Property::dealDate.name, property.dealDate)
                .setOnInsert(Property::address.name, property.address)
                .setOnInsert(Property::addressNumber.name, property.addressNumber)
                .setOnInsert(Property::houseType.name, property.houseType)
                .setOnInsert(Property::buildYear.name, property.buildYear)
                .setOnInsert(Property::exclusiveArea.name, property.exclusiveArea)
                .setOnInsert(Property::location.name, property.location)

            if (property.tradeType == TradeType.MONTHLY_RENT) {
                update.setOnInsert("rentPrice", (property as MonthlyRent).rentPrice)
            }

            mongoTemplate.upsert(query, update, Property::class.java)
        }
    }

    fun createGeoIndex() {
        mongoTemplate.indexOps(Property::class.java)
            .ensureIndex(GeospatialIndex(Property::location.name).typed(GeoSpatialIndexType.GEO_2DSPHERE))
    }

    fun findTotalSize() = mongoTemplate.count(Query(), Property::class.java)

    fun pagingProperties(propertyCondition: PropertyCondition, latitude: Double, longitude: Double): List<PropertyResponse> {
        val criteria = Criteria()
        val orCriteria = mutableListOf<Criteria>()

        propertyCondition.tradeType.forEach { tradeType ->
            when (tradeType) {
                TradeType.SALE, TradeType.LONG_TERM_RENT -> {
                    orCriteria.add(
                        Criteria().andOperator(
                            Criteria(Property::tradeType.name).`is`(tradeType.name),
                            Criteria(Property::price.name).gte(propertyCondition.minPrice / 10_000)
                                .lte(propertyCondition.maxPrice / 10_000)
                        )
                    )
                }
                TradeType.MONTHLY_RENT -> {
                    orCriteria.add(
                        Criteria().andOperator(
                            Criteria(Property::tradeType.name).`is`(tradeType.name),
                            Criteria(Property::price.name).gte(propertyCondition.minPrice / 10_000)
                                .lte(propertyCondition.maxPrice / 10_000),
                            Criteria(Property::rentPrice.name).gte(propertyCondition.minRentPrice / 10_000)
                                .lte(propertyCondition.maxRentPrice / 10_000)
                        )
                    )
                }
            }
        }

        if (orCriteria.isNotEmpty()) {
            criteria.orOperator(*orCriteria.toTypedArray())
        }
        val point = Point(longitude, latitude)

        val query = NearQuery.near(point)
            .maxDistance(propertyCondition.travelTime * 0.4, Metrics.KILOMETERS)
            .spherical(true)
            .query(Query(criteria))

        val geoResults = mongoTemplate.geoNear(query, Property::class.java)

        return geoResults.map { PropertyResponse.from(it.content, it.distance) }
    }

    fun findAllByAddresses(addresses: List<String>): List<Property> {
        val query = Query(Criteria.where("address").`in`(addresses))
        return mongoTemplate.find(query, Property::class.java)
    }

    fun deleteAll() {
        val query = Query()
        mongoTemplate.remove(query, Property::class.java)
    }
}