package com.example.mpdriver.variables

import android.util.Log
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.format.char
import java.time.format.DateTimeFormatter

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

val datetimeFormatFromJava = DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm:ss")
