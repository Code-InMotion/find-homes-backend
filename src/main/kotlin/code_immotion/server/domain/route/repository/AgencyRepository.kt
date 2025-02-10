package code_immotion.server.domain.route.repository

import code_immotion.server.domain.route.entity.Agency
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface AgencyRepository : JpaRepository<Agency, String> {
    @Modifying
    @Query("DELETE FROM Agency")
    fun deleteAllBatch()
}
