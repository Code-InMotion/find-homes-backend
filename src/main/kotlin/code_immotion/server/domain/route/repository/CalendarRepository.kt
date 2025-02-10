package code_immotion.server.domain.route.repository

import code_immotion.server.domain.route.entity.Calendar
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface CalendarRepository : JpaRepository<Calendar, String>{
    @Modifying
    @Query("DELETE FROM Calendar")
    fun deleteAllBatch()
}