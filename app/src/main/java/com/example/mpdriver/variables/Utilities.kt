package com.example.mpdriver.variables

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char


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
