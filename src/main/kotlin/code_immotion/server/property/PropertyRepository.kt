package code_immotion.server.property

import code_immotion.server.property.dto.PropertyAggregation
import code_immotion.server.property.dto.PropertyCondition
import code_immotion.server.property.entity.MonthlyRent
import code_immotion.server.property.entity.Property
import code_immotion.server.property.entity.TradeType
import org.springframework.data.domain.Sort
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
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
                    Criteria.where(Property::addressNumber.name).`is`(property.addressNumber),
                    Criteria.where(Property::floor.name).`is`(property.floor),
                    Criteria.where(Property::houseType.name).`is`(property.houseType.name),
                    Criteria.where(Property::exclusiveArea.name).`is`(property.houseType.name)
                )
            )

            val update = Update()
                .setOnInsert(Property::price.name, property.price)
                .setOnInsert(Property::floor.name, property.floor)
                .setOnInsert(Property::dealDate.name, property.dealDate)
                .setOnInsert(Property::houseType.name, property.houseType)
                .setOnInsert(Property::buildYear.name, property.buildYear)
                .setOnInsert(Property::exclusiveArea.name, property.exclusiveArea)
                .setOnInsert(Property::location.name, property.location)

            if (property.tradeType == TradeType.MONTHLY_RENT) {
                update.setOnInsert("rentPrice", (property as MonthlyRent).rentPrice)
            }

            mongoTemplate.upsert(query, update, Property::class.java)
            createGeoIndex()
        }
    }

    private fun createGeoIndex() {
        mongoTemplate.indexOps(Property::class.java)
            .ensureIndex(GeospatialIndex(Property::location.name).typed(GeoSpatialIndexType.GEO_2DSPHERE))
    }

    fun findTotalSize() = mongoTemplate.count(Query(), Property::class.java)

    fun findRegionWithCondition(condition: PropertyCondition, latitude: Double, longitude: Double): List<PropertyAggregation> {
        val criteria = condition.tradeType.map { tradeType ->
            val conditions = mutableListOf(
                Criteria(Property::tradeType.name).`is`(tradeType.name),
                Criteria(Property::price.name).gte(condition.minPrice / 10_000).lte(condition.maxPrice / 10_000)
            )

            if (tradeType == TradeType.MONTHLY_RENT) {
                conditions.add(
                    Criteria(Property::rentPrice.name).gte(condition.minRentPrice / 10_000).lte(condition.maxRentPrice / 10_000)
                )
            }
            Criteria().andOperator(*conditions.toTypedArray())
        }.let { Criteria().orOperator(*it.toTypedArray()) }

        val geoNearOperation = Aggregation.geoNear(
            NearQuery.near(Point(longitude, latitude))
                .maxDistance(condition.travelTime * 0.4, Metrics.KILOMETERS)
                .spherical(true)
                .distanceMultiplier(0.001),
            "distance"
        )

        val matchOperation = Aggregation.match(criteria)
        val groupOperation = Aggregation.group(Property::address.name, Property::addressNumber.name, Property::houseType.name, Property::tradeType.name)
            .count().`as`("propertyCount")
            .avg("price").`as`("averagePrice")
            .avg("distance").`as`("averageDistance")
            .first("address").`as`("address")
        val sortOperation = Aggregation.sort(Sort.Direction.DESC, "propertyCount")
        val limitOperation = Aggregation.limit(5)

        val aggregation = Aggregation.newAggregation(
            geoNearOperation,
            matchOperation,
            groupOperation,
            sortOperation,
            limitOperation
        )

        return mongoTemplate.aggregate(
            aggregation,
            Property::class.java,
            PropertyAggregation::class.java
        ).mappedResults
    }

    fun findAllByAddresses(addresses: List<String>): List<Property> {
        val query = Query(Criteria.where("address").`in`(addresses))
        return mongoTemplate.find(query, Property::class.java)
    }

    fun deleteAll() = mongoTemplate.remove(Query(), Property::class.java)

}