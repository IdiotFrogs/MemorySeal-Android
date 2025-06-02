package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.resource.R
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil

@Composable
fun MSCalender(
    onDateSelected: (LocalDate) -> Unit
) {
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }

    val today = LocalDate.now()
    val currentMonth = YearMonth.from(today)
    val canGoToPrevMonth = currentYearMonth > currentMonth

    val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")

    val firstDayOfMonth = currentYearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 일요일 = 0

    val prevMonth = currentYearMonth.minusMonths(1)
    val prevMonthLength = prevMonth.lengthOfMonth()
    val leadingDates = (0 until firstDayOfWeek).map {
        prevMonth.atDay(prevMonthLength - firstDayOfWeek + it + 1)
    }

    val currentMonthDates = (1..currentYearMonth.lengthOfMonth()).map {
        currentYearMonth.atDay(it)
    }

    val totalCells = leadingDates.size + currentMonthDates.size
    val rowCount = ceil(totalCells / 7.0).toInt()

    val trailingCount = rowCount * 7 - totalCells
    val nextMonth = currentYearMonth.plusMonths(1)
    val trailingDates = (1..trailingCount).map { nextMonth.atDay(it) }

    val dates = leadingDates + currentMonthDates + trailingDates

    Column(
        modifier = Modifier
            .border(1.dp, MSTheme.color.greyG2, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            MSText(
                text = "${currentYearMonth.year}년 ${currentYearMonth.monthValue}월",
                fontSize = 14.dp,
                color = MSTheme.color.greyG5
            )
            Spacer(Modifier.weight(1f))
            Icon(
                modifier = Modifier.noRippleClickable {
                    if (canGoToPrevMonth) currentYearMonth = currentYearMonth.minusMonths(1)
                },
                painter = painterResource(R.drawable.ic_calender_before),
                contentDescription = "이전 달",
                tint = if (canGoToPrevMonth) Color.Unspecified else MSTheme.color.greyG2
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                modifier = Modifier.noRippleClickable {
                    currentYearMonth = currentYearMonth.plusMonths(1)
                },
                painter = painterResource(R.drawable.ic_calender_after),
                contentDescription = "다음 달"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (day in daysOfWeek) {
                MSText(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = MSTheme.color.greyG4,
                    fontSize = 12.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height((rowCount * 50).dp)
        ) {
            items(dates) { date ->
                val isCurrentMonth = date.month == currentYearMonth.month
                val isSelected = date == selectedDate.value
                val isPast = date.isBefore(today)

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isSelected -> MSTheme.color.primaryNormal
                                else -> Color.Transparent
                            }
                        )
                        .noRippleClickable {
                            if (!isPast) {
                                selectedDate.value = date

                                val selectedMonth = YearMonth.from(date)
                                if (selectedMonth != currentYearMonth) {
                                    currentYearMonth = selectedMonth
                                }

                                onDateSelected(date)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    MSText(
                        text = date.dayOfMonth.toString(),
                        color = when {
                            isSelected -> MSTheme.color.white
                            isCurrentMonth -> MSTheme.color.greyG5
                            else -> MSTheme.color.greyG2
                        }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun MsCalenderPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MSCalender {}
    }
}