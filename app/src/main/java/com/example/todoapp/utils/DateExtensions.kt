package com.example.todoapp.utils

import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

fun Date?.toLocalDate(): LocalDate? {
    return this?.let {
        this.toInstant()
            .atZone(ZoneId.of("UTC"))
            .toLocalDate()
    }
}

fun LocalDate.toDate(): Date {
    return Date.from(
        this.atStartOfDay(ZoneId.of("UTC"))
            .toInstant()
    )
}