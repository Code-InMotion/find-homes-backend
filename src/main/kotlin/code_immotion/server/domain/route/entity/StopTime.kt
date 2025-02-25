package code_immotion.server.domain.route.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime
import java.time.LocalTime


@Entity
@IdClass(StopTimeId::class)
class StopTime(
    @Id
    @ManyToOne
    val trip: Trip,

    @Id
    @ManyToOne
    val stop: Stop,

    @Id
    val stopSequence: Int,

    val arrivalTime: LocalTime,
    val departureTime: LocalTime,
    val pickupType: Int, // 0: 승차 가능, 1: 승차 불가
    val dropOffType: Int, // 0: 하차 가능, 1: 하차 불가
    val timePoint: Int,  // 1: 정확한 시각
    val updatedAt: LocalDateTime = LocalDateTime.now()
)