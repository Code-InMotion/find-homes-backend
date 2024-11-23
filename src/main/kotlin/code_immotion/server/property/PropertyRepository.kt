package code_immotion.server.property

import code_immotion.server.property.dto.PropertyPagingParam
import code_immotion.server.property.entity.Property
import code_immotion.server.property.entity.Trade
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
                    Criteria.where("trade._type").`is`(property.trade.type.name)
                )
            )

            val update = Update().set("trade._type", property.trade.type.name)
                .set("trade._price", property.trade.price)
                .set("trade._floor", property.trade.floor)
                .set("trade._dealDate", property.trade.dealDate)
                .setOnInsert("address", property.address)
                .setOnInsert("houseType", property.houseType)
                .setOnInsert("buildYear", property.buildYear)
                .setOnInsert("exclusiveArea", property.exclusiveArea)
                .setOnInsert("latitude", property.latitude)
                .setOnInsert("longitude", property.longitude)

            if (property.trade is Trade.MonthlyRent) {
                update.set("trade.monthlyPrice", property.trade.monthlyPrice)
            }

            mongoTemplate.upsert(query, update, Property::class.java)
        }

    }

    fun findTotalSize() = mongoTemplate.count(Query(), Property::class.java)

    fun pagingProperties(pagingParam: PropertyPagingParam): PageImpl<Property> {
        val pageable = pagingParam.toPageable()
        val priceConditions = pagingParam.tradeType?.map { tradeType ->
            when (tradeType) {
                TradeType.SALE, TradeType.LONG_TERM_RENT -> {
                    Criteria.where("trade._price").gte(pagingParam.minPrice / 10_000).lte(pagingParam.maxPrice / 10_000)
                        .and("trade._type").`is`(tradeType)
                }

                TradeType.MONTHLY_RENT -> {
                    Criteria().andOperator(
                        Criteria.where("trade._price").gte(pagingParam.minPrice / 10_000)
                            .lte(pagingParam.maxPrice / 10_000),
                        Criteria.where("trade._rentPrice").gte(pagingParam.minRentPrice / 10_000)
                            .lte(pagingParam.maxRentPrice / 10_000),
                        Criteria.where("trade._type").`is`(tradeType)
                    )
                }
            }
        }

        val criteria = priceConditions?.let {
            Criteria().orOperator(it)
                .and("houseType").`in`(pagingParam.houseType)
        }

        val query = criteria?.let { Query(it).with(pageable) } ?: Query().with(pageable)


        val total = mongoTemplate.count(Query(), Property::class.java)
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