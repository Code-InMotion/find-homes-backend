package code_immotion.server.domain.route.repository

import code_immotion.server.domain.route.entity.StopTime
import code_immotion.server.domain.route.entity.StopTimeId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface StopTimeRepository : JpaRepository<StopTime, StopTimeId>