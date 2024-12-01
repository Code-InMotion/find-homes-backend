package code_immotion.server.property

import code_immotion.server.property.dto.PropertyAggregation
import code_immotion.server.property.dto.PropertyCondition
import code_immotion.server.property.dto.PropertyResponse
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
    fun upsertAll(properties: List<Property>) {
        properties.forEach { property ->
            val query = Query(
                Criteria().andOperator(
                    Criteria.where(Property::address.name).`is`(property.address),
                    Criteria.where(Property::addressNumber.name).`is`(property.addressNumber),
                    Criteria.where(Property::floor.name).`is`(property.floor),
                    Criteria.where(Property::houseType.name).`is`(property.houseType.name),
                    Criteria.where(Property::tradeType.name).`is`(property.tradeType.name),
                    Criteria.where(Property::exclusiveArea.name).`is`(property.houseType.name)
                )
            )

            val update = Update()
                .setOnInsert(Property::address.name, property.address)
                .setOnInsert(Property::addressNumber.name, property.addressNumber)
                .setOnInsert(Property::price.name, property.price)
                .setOnInsert(Property::floor.name, property.floor)
                .setOnInsert(Property::dealDate.name, property.dealDate)
                .setOnInsert(Property::houseType.name, property.houseType)
                .setOnInsert(Property::tradeType.name, property.tradeType)
                .setOnInsert(Property::buildYear.name, property.buildYear)
                .setOnInsert(Property::exclusiveArea.name, property.exclusiveArea)
                .setOnInsert(Property::location.name, property.location)

            if (property.tradeType == TradeType.MONTHLY_RENT) {
                update.setOnInsert("rentPrice", (property as MonthlyRent).rentPrice)
            }

            createGeoIndex()
            mongoTemplate.upsert(query, update, Property::class.java)
        }
    }

    fun findTotalSize() = mongoTemplate.count(Query(), Property::class.java)

    fun findProperty(propertyId: String, point: Point): PropertyResponse? {
        val nearQuery = NearQuery.near(point)
            .spherical(true)
            .distanceMultiplier(6371.0)

        val criteria = Criteria.where(Property::id.name).`is`(propertyId)
        nearQuery.query(Query.query(criteria))

        val geoResults = mongoTemplate.geoNear(
            nearQuery,
            Property::class.java,
            "property"
        )

        return geoResults.firstOrNull()?.let { geoResult ->
            PropertyResponse.from(
                property = geoResult.content,
                distance = geoResult.distance
            )
        }
    }

    fun findRegionWithCondition(condition: PropertyCondition, point: Point): List<PropertyAggregation> {
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
            NearQuery.near(point)
                .maxDistance(condition.travelTime * 0.4, Metrics.KILOMETERS)
                .spherical(true),
            "distance"
        )

        val matchOperation = Aggregation.match(criteria)
        val groupOperation = Aggregation.group(Property::address.name)
            .count().`as`(PropertyAggregation::propertyCount.name)
            .avg(Property::price.name).`as`(PropertyAggregation::averagePrice.name)
            .avg("distance").`as`(PropertyAggregation::averageDistance.name)
            .first(Property::address.name).`as`(PropertyAggregation::address.name)
        val sortOperation = Aggregation.sort(Sort.Direction.DESC, PropertyAggregation::propertyCount.name)
            .and(Sort.Direction.ASC, condition.sortType.value)
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

    private fun createGeoIndex() {
        mongoTemplate.indexOps(Property::class.java)
            .ensureIndex(GeospatialIndex(Property::location.name).typed(GeoSpatialIndexType.GEO_2DSPHERE))
    }
}