package code_immotion.server.property

import code_immotion.server.property.dto.PagingPropertyResponse
import code_immotion.server.property.dto.PropertyCondition
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
                    Criteria.where("address").`is`(property.address),
                    Criteria.where("type").`is`(property.type.name)
                )
            )

            val update = Update()
                .setOnInsert("price", property.price)
                .setOnInsert("floor", property.floor)
                .setOnInsert("dealDate", property.dealDate)
                .setOnInsert("address", property.address)
                .setOnInsert("addressNumber", property.addressNumber)
                .setOnInsert("houseType", property.houseType)
                .setOnInsert("buildYear", property.buildYear)
                .setOnInsert("exclusiveArea", property.exclusiveArea)
                .setOnInsert("location", property.location)

            if (property.type == TradeType.MONTHLY_RENT) {
                update.setOnInsert("monthlyPrice", (property as MonthlyRent).monthlyPrice)
            }

            mongoTemplate.upsert(query, update, Property::class.java)
        }
    }

    fun createGeoIndex() {
        mongoTemplate.indexOps(Property::class.java)
            .ensureIndex(GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE))
    }

    fun findTotalSize() = mongoTemplate.count(Query(), Property::class.java)

    fun pagingProperties(pagingParam: PropertyCondition, latitude: Double, longitude: Double): PagingPropertyResponse {
        val pageable = pagingParam.toPageable()
        var criteria = Criteria()
        val orCriteria = mutableListOf<Criteria>()

        pagingParam.tradeType.forEach { tradeType ->
            when (tradeType) {
                TradeType.SALE, TradeType.LONG_TERM_RENT -> {
                    orCriteria.add(
                        Criteria().andOperator(
                            Criteria("type").`is`(tradeType),
                            Criteria("price").gte(pagingParam.minPrice / 10_000)
                                .lte(pagingParam.maxPrice / 10_000)
                        )
                    )
                }

                TradeType.MONTHLY_RENT -> {
                    orCriteria.add(
                        Criteria().andOperator(
                            Criteria("type").`is`(tradeType),
                            Criteria("price").gte(pagingParam.minPrice / 10_000)
                                .lte(pagingParam.maxPrice / 10_000),
                            Criteria("rentPrice").gte(pagingParam.minRentPrice / 10_000)
                                .lte(pagingParam.maxRentPrice / 10_000)
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
            .maxDistance(pagingParam.travelTime * 0.4, Metrics.KILOMETERS)
            .spherical(true)
            .query(Query(criteria).with(pageable))

        val geoResults = mongoTemplate.geoNear(query, Property::class.java)

        return PagingPropertyResponse.from(geoResults.content, geoResults.count(), pageable.pageNumber)
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