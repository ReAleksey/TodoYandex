package com.example.todoapp.utils

import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

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