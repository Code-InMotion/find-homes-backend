package code_immotion.server.domain.route.repository

import code_immotion.server.domain.route.entity.Trip
import org.springframework.data.jpa.repository.JpaRepository

interface TripRepository : JpaRepository<Trip, String>