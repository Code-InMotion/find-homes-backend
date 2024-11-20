package code_immotion.server.property

import code_immotion.server.property.entity.Property
import org.springframework.data.domain.PageImpl
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class PropertyRepository(private val mongoTemplate: MongoTemplate) {
    fun pagingProperties(pagingParam: PropertyPagingParam): PageImpl<Property> {
        val pageable = pagingParam.toPageable()
        val query = Query().with(pageable)
        val total = mongoTemplate.count(Query(), Property::class.java)
        val properties = mongoTemplate.find(query, Property::class.java)

        return PageImpl(properties, pageable, total)
    }

    fun bulkInsert(properties: List<Property>) {
        mongoTemplate.insertAll(properties)
    }

    fun deleteAll() {
        val query = Query()
        mongoTemplate.remove(query, Property::class.java)
    }
}