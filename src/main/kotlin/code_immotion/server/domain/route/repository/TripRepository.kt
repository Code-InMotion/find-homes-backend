package code_immotion.server.domain.route.repository

import code_immotion.server.domain.route.entity.Trip
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface TripRepository : JpaRepository<Trip, String>