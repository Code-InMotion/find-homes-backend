package code_immotion.server.property

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Property(
    @Id
    @Tsid
    val id: Long? = null,

    val name: String
//    val
)