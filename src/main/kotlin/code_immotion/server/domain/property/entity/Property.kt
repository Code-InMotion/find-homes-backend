package code_immotion.server.domain.property.entity

import code_immotion.server.application.handler.exception.CustomException
import code_immotion.server.application.handler.exception.ErrorCode
import code_immotion.server.application.api.client.ApiLink
import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Property(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    val buildingName: String,
    val address: String,
    val addressNumber: String,
    val buildYear: Int,
    val exclusiveArea: Int,

    @Embedded
    var geoLocation: GeoLocation? = null,

    @OneToMany(mappedBy = "property")
    val sales: MutableList<Sale> = mutableListOf(),

    @OneToMany(mappedBy = "property")
    val rents: MutableList<Rent> = mutableListOf()
) {
    companion object {
        private fun propertyInit(jsonNode: JsonNode, state: String, city: String) = Property(
            buildingName = when {
                jsonNode.hasNonNull("offiNm") -> jsonNode.path("offiNm").asText()
                jsonNode.hasNonNull("aptNm") -> jsonNode.path("aptNm").asText()
                jsonNode.hasNonNull("mhouseNm") -> jsonNode.path("mhouseNm").asText()
                else -> throw CustomException(ErrorCode.BAD_REQUEST)
            },
            address = "$state $city ${jsonNode.path("umdNm").asText()}",
            addressNumber = jsonNode.path("jibun").asText(),
            buildYear = jsonNode.path("buildYear").asInt(),
            exclusiveArea = jsonNode.path("excluUseAr").asDouble().toInt(),
        )

        fun fromSale(items: Iterable<JsonNode>, state: String, city: String, link: ApiLink): MutableList<Property> {
            return items.map { item ->
                val property = propertyInit(item, state, city)

                val sale = Sale(
                    floor = item.path("floor").asInt(),
                    houseType = when (link) {
                        ApiLink.OFFICETEL -> HouseType.OFFICETEL
                        ApiLink.APARTMENT -> HouseType.APARTMENT
                        else -> HouseType.VILLA
                    },
                    price = item.path("dealAmount").asText().replace(",", "").toLong(),
                    dealDate = LocalDate.of(
                        item.path("dealYear").asInt(),
                        item.path("dealMonth").asInt(),
                        item.path("dealDay").asInt()
                    ),
                    property = property
                )

                property.also { it.sales.add(sale) }
            }.toMutableList()
        }

        fun fromRent(items: Iterable<JsonNode>, state: String, city: String, link: ApiLink): MutableList<Property> {
            return items.map { item ->
                val property = propertyInit(item, state, city)
                val rentPrice = item.path("monthlyRent").asText().replace(",", "").toLong()
                val rent = Rent(
                    floor = item.path("floor").asInt(),
                    houseType = when (link) {
                        ApiLink.OFFICETEL -> HouseType.OFFICETEL
                        ApiLink.APARTMENT -> HouseType.APARTMENT
                        else -> HouseType.VILLA
                    },
                    price = item.path("deposit").asText().replace(",", "").toLong(),
                    rentPrice = rentPrice,
                    dealDate = LocalDate.of(
                        item.path("dealYear").asInt(),
                        item.path("dealMonth").asInt(),
                        item.path("dealDay").asInt()
                    ),
                    property = property,
                    isMonthly = rentPrice > 0
                )

                property.also { it.rents.add(rent) }
            }.toMutableList()
        }
    }
}