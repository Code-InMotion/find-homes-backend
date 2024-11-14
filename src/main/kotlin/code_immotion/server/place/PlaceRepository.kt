package code_immotion.server.place

import org.springframework.data.mongodb.repository.MongoRepository

interface PlaceRepository : MongoRepository<Place, String>