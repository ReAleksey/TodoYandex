package com.example.todoapp.utils

import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

fun Date?.toLocalDate(): LocalDate? {
    return this?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
}