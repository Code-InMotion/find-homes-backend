package code_immotion.server.property

import code_immotion.server.property.domain.Property
import org.springframework.data.mongodb.repository.MongoRepository


interface PropertyRepository : MongoRepository<Property, String>