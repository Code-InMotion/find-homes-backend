package code_immotion.server.domain.route.repository

import code_immotion.server.domain.route.entity.StopTime
import org.springframework.data.jpa.repository.JpaRepository

interface StopTimeRepository : JpaRepository<StopTime, Long>