package com.idiotfrogs.extension

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun LocalDateTime.toYearMonthDay(): String =
    "${year}. ${month.number.toString().padStart(2, '0')}. ${day.toString().padStart(2, '0')}"

@OptIn(ExperimentalTime::class)
fun LocalDateTime.toDday(): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val targetDate = this.date
    val diff = targetDate.toEpochDays() - today.toEpochDays()

    return if (diff == 0L) "D-day" else "D-$diff"
}