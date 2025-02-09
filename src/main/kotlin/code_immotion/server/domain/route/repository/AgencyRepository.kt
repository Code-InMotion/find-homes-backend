package code_immotion.server.domain.route.repository

import code_immotion.server.domain.route.entity.Agency
import org.springframework.data.jpa.repository.JpaRepository

interface AgencyRepository : JpaRepository<Agency, String>