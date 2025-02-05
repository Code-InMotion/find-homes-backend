package code_immotion.server.domain.route.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalTime


@Entity
class StopTime(
    @Id
    @GeneratedValue
    val id: Long,
    @ManyToOne
    val trip: Trip,
    @ManyToOne
    val stop: Stop,
    val arrivalTime: LocalTime,
    val departureTime: LocalTime,
    val stopSequence: Int,
    val pickupType: Int, // 0: 승차 가능, 1: 승차 불가
    val dropOffType: Int, // 0: 하차 가능, 1: 하차 불가
    val timePoint: Int  // 1: 정확한 시각
)