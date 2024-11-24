package code_immotion.server.property

import code_immotion.server.property.dto.PropertyPagingParam
import code_immotion.server.property.entity.MonthlyRent
import code_immotion.server.property.entity.Property
import code_immotion.server.property.entity.TradeType
import org.springframework.data.domain.PageImpl
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
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
                .setOnInsert("trade._type", property.type.name)
                .setOnInsert("price", property.price)
                .setOnInsert("floor", property.floor)
                .setOnInsert("dealDate", property.dealDate)
                .setOnInsert("address", property.address)
                .setOnInsert("houseType", property.houseType)
                .setOnInsert("buildYear", property.buildYear)
                .setOnInsert("exclusiveArea", property.exclusiveArea)
                .setOnInsert("latitude", property.latitude)
                .setOnInsert("longitude", property.longitude)

            if (property.type == TradeType.MONTHLY_RENT) {
                update.setOnInsert("monthlyPrice", (property as MonthlyRent).monthlyPrice)
            }

            mongoTemplate.upsert(query, update, Property::class.java)
        }
    }

    fun findTotalSize() = mongoTemplate.count(Query(), Property::class.java)

    fun pagingProperties(pagingParam: PropertyPagingParam): PageImpl<Property> {
        val pageable = pagingParam.toPageable()
        val criteria = Criteria()

        pagingParam.tradeType.forEach { tradeType ->
            when (tradeType) {
                TradeType.SALE, TradeType.LONG_TERM_RENT -> {
                    criteria.and("price").gte(pagingParam.minPrice / 10_000)
                        .lte(pagingParam.maxPrice / 10_000)
                        .and("type").`is`(tradeType)
                }

                TradeType.MONTHLY_RENT -> {
                    criteria.and("price").gte(pagingParam.minPrice / 10_000).lte(pagingParam.maxPrice / 10_000)
                        .and("rentPrice").gte(pagingParam.minRentPrice / 10_000).lte(pagingParam.maxRentPrice / 10_000)
                        .and("type").`is`(tradeType)
                }
            }
        }

        val query = Query(criteria).with(pageable)

        val total = mongoTemplate.count(query, Property::class.java)
        val properties = mongoTemplate.find(query, Property::class.java)

        return PageImpl(properties, pageable, total)
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