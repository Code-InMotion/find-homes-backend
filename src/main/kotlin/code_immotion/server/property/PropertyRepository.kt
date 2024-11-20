package code_immotion.server.property

import code_immotion.server.property.dto.PropertyPagingParam
import code_immotion.server.property.entity.Property
import code_immotion.server.property.entity.TradeType
import org.springframework.data.domain.PageImpl
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class PropertyRepository(private val mongoTemplate: MongoTemplate) {
    fun pagingProperties(pagingParam: PropertyPagingParam): PageImpl<Property> {
        val pageable = pagingParam.toPageable()
        val priceConditions = pagingParam.tradeType?.map { tradeType ->
            when (tradeType) {
                TradeType.SALE, TradeType.LONG_TERM_RENT -> {
                    Criteria.where("trade.price").gte(pagingParam.minPrice / 10_000).lte(pagingParam.maxPrice / 10_000)
                        .and("trade.type").`is`(tradeType)
                }

                TradeType.MONTHLY_RENT -> {
                    Criteria().andOperator(
                        Criteria.where("trade.price").gte(pagingParam.minPrice / 10_000).lte(pagingParam.maxPrice / 10_000),
                        Criteria.where("trade.rentPrice").gte(pagingParam.minRentPrice / 10_000).lte(pagingParam.maxRentPrice / 10_000),
                        Criteria.where("trade.type").`is`(tradeType)
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

    fun bulkInsert(properties: List<Property>) {
        mongoTemplate.insertAll(properties)
    }

    fun findByAddress(address: String): Property? {
        val query = Query(Criteria.where("address").`is`(address))
        return mongoTemplate.findOne(query, Property::class.java)
    }

    fun findAll() = mongoTemplate.findAll(Property::class.java)

    fun deleteAll() {
        val query = Query()
        mongoTemplate.remove(query, Property::class.java)
    }
}