package com.idiotfrogs.extension

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun LocalDateTime?.toYearMonthDay(): String =
    "${this?.year}. ${this?.month?.number.toString().padStart(2, '0')}. ${this?.day.toString().padStart(2, '0')}"

fun LocalDateTime?.toYearMonthDayWeek(): String =
    "${this?.year}. ${this?.month?.number.toString().padStart(2, '0')}. ${this?.day.toString().padStart(2, '0')}. ${this?.dayOfWeek?.toKoreanShort()}"

@OptIn(ExperimentalTime::class)
fun LocalDateTime?.toDday(): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val targetDate = this?.date
    val diff = targetDate?.toEpochDays()?.minus(today.toEpochDays())

    return "D-$diff"
}

private fun DayOfWeek.toKoreanShort(): String = when (this) {
    DayOfWeek.MONDAY -> "(월)"
    DayOfWeek.TUESDAY -> "(화)"
    DayOfWeek.WEDNESDAY -> "(수)"
    DayOfWeek.THURSDAY -> "(목)"
    DayOfWeek.FRIDAY -> "(금)"
    DayOfWeek.SATURDAY -> "(토)"
    DayOfWeek.SUNDAY -> "(일)"
}