package code_immotion.server.place

import org.springframework.stereotype.Service

@Service
class PlaceService(
    private val placeRepository: PlaceRepository
) {
    fun saveAll(places: List<Place>) {
        placeRepository.saveAll(places)
    }

    fun readAll() = placeRepository.findAll()

    fun deleteAll() = placeRepository.deleteAll()
}