package code_immotion.server.domain.route.repository

import code_immotion.server.domain.route.entity.Calendar
import org.springframework.data.jpa.repository.JpaRepository

interface CalendarRepository : JpaRepository<Calendar, String>