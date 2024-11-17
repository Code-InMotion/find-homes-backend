package code_immotion.server.property.entity

import java.time.LocalDate
import java.time.format.DateTimeFormatter

abstract class Trade {
    abstract val type: TradeType
    abstract val floor: Int
    abstract val price: Long
    abstract val dealDate: LocalDate

    companion object {
        fun parseContractDate(dateStr: String): LocalDate {
            val yearMonth = dateStr.split(".")
            val year = "20${yearMonth[0]}"
            val month = yearMonth[1].padStart(2, '0')
            return LocalDate.parse("$year-$month-01", DateTimeFormatter.ISO_DATE)
        }
    }
}