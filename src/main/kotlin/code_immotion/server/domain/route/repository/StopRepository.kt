package code_immotion.server.domain.route.repository

import code_immotion.server.domain.route.entity.Stop
import org.springframework.data.jpa.repository.JpaRepository

interface StopRepository : JpaRepository<Stop, String>