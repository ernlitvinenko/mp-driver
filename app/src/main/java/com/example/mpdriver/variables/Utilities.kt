package com.example.mpdriver.variables

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.format.char
import java.time.format.DateTimeFormatter

val version = "0.0.1alpha-private"
val VC = 16

class PersonalDatetimeFormat(private val pattern: String) {
    fun toJava(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern(pattern)
    }

    fun toKotlin(): DateTimeFormat<LocalDateTime> {
        return LocalDateTime.Format {
            byUnicodePattern(pattern)
        }
    }
}

val datetimeFormatFrom = LocalDateTime.Format {
    dayOfMonth()
    char('.')
    monthNumber()
    char('.')
    year()
    char(' ')
    hour(padding = Padding.NONE)
    char(':')
    minute()
    char(':')
    second()
}
val timeFormat = PersonalDatetimeFormat("HH:mm")
val dateFormat = PersonalDatetimeFormat("dd.MM.yyyy")

