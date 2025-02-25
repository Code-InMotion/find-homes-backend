package code_immotion.server.domain.property

import code_immotion.server.domain.property.dto.PropertyAggregation
import code_immotion.server.domain.property.dto.PropertyCondition
import code_immotion.server.domain.property.dto.PropertyResponse
import code_immotion.server.domain.property.entity.MonthlyRent
import code_immotion.server.domain.property.entity.Property
import code_immotion.server.domain.property.entity.TradeType
import org.springframework.cache.annotation.Cacheable
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
                .setOnInsert(Property::buildingName.name, property.buildingName)
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

    @Cacheable(
        cacheNames = ["region-stats"],
        key = "#condition.hashCode() + '_' + #point.toString()"
    )
    fun findRegionWithCondition(condition: PropertyCondition.Recommend, point: Point): List<PropertyAggregation.Data> {
        val criteria = createPropertyCriteria(condition)

        val propertiesProjection = org.bson.Document(
            mapOf(
                "_id" to "\$_id",
                PropertyResponse::price.name to "\$${PropertyResponse::price.name}",
                PropertyResponse::buildingName.name to "\$${PropertyResponse::buildingName.name}",
                PropertyResponse::rentPrice.name to "\$${PropertyResponse::rentPrice.name}",
                PropertyResponse::tradeType.name to "\$${PropertyResponse::tradeType.name}",
                PropertyResponse::address.name to "\$${PropertyResponse::address.name}",
                PropertyResponse::addressNumber.name to "\$${PropertyResponse::addressNumber.name}",
                PropertyResponse::floor.name to "\$${PropertyResponse::floor.name}",
                PropertyResponse::houseType.name to "\$${PropertyResponse::houseType.name}",
                PropertyResponse::dealDate.name to "\$${PropertyResponse::dealDate.name}",
                PropertyResponse::exclusiveArea.name to "\$${PropertyResponse::exclusiveArea.name}",
                PropertyResponse::buildYear.name to "\$${PropertyResponse::buildYear.name}",
                PropertyResponse::location.name to "\$${PropertyResponse::location.name}",
                PropertyResponse::distance.name to "\$distance"
            )
        )

        val aggregation = Aggregation.newAggregation(
            Aggregation.geoNear(
                NearQuery.near(point)
                    .maxDistance(condition.travelTime * 0.4, Metrics.KILOMETERS)
                    .spherical(true),
                PropertyResponse::distance.name
            ),
            Aggregation.match(criteria),
            Aggregation.group(Property::address.name)
                .count().`as`(PropertyAggregation.Data::propertyCount.name)
                .avg(Property::price.name).`as`(PropertyAggregation.Data::averagePrice.name)
                .avg("distance").`as`(PropertyAggregation.Data::averageDistance.name)
                .first(Property::address.name).`as`(PropertyAggregation.Data::address.name)
                .push(propertiesProjection).`as`(PropertyAggregation.Data::properties.name),
            Aggregation.sort(Sort.Direction.DESC, PropertyAggregation.Data::propertyCount.name)
                .and(Sort.Direction.ASC, condition.sortType.value),
            Aggregation.limit(5)
        )

        return mongoTemplate.aggregate(
            aggregation,
            Property::class.java,
            PropertyAggregation.Data::class.java
        ).mappedResults
    }

    @Cacheable(
        cacheNames = ["region-properties"],
        key = "#address + '_' + #condition.hashCode() + '_' + #point.toString()"
    )
    fun findPropertiesByRegion(
        condition: PropertyCondition.Address,
        point: Point
    ): List<PropertyResponse> {
        val recommendCondition = PropertyCondition.Recommend.from(condition)
        val criteria = createPropertyCriteria(recommendCondition)
            .and(Property::address.name).`is`(condition.address)

        val aggregation = Aggregation.newAggregation(
            Aggregation.geoNear(
                NearQuery.near(point)
                    .maxDistance(condition.travelTime * 0.4, Metrics.KILOMETERS)
                    .spherical(true),
                PropertyResponse::distance.name
            ),
            Aggregation.match(criteria),
            Aggregation.project()
                .andExpression("_id").`as`(PropertyResponse::id.name)
                .andInclude(
                    PropertyResponse::id.name,
                    PropertyResponse::price.name,
                    PropertyResponse::rentPrice.name,
                    PropertyResponse::tradeType.name,
                    PropertyResponse::address.name,
                    PropertyResponse::addressNumber.name,
                    PropertyResponse::floor.name,
                    PropertyResponse::houseType.name,
                    PropertyResponse::dealDate.name,
                    PropertyResponse::exclusiveArea.name,
                    PropertyResponse::buildYear.name,

                    )
                .and("distance").`as`("distance"),
            Aggregation.sort(Sort.Direction.ASC, condition.sortType.value)
        )

        return mongoTemplate.aggregate(
            aggregation,
            Property::class.java,
            PropertyResponse::class.java
        ).mappedResults
    }

    private fun createPropertyCriteria(condition: PropertyCondition.Recommend) =
        condition.tradeType.map { tradeType ->
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

    fun deleteAll() = mongoTemplate.remove(Query(), Property::class.java)

    private fun createGeoIndex() {
        mongoTemplate.indexOps(Property::class.java)
            .ensureIndex(GeospatialIndex(Property::location.name).typed(GeoSpatialIndexType.GEO_2DSPHERE))
    }
}