package com.example.alarmscratch.extension

import com.example.alarmscratch.data.model.WeeklyRepeater
import java.time.DayOfWeek

// DayOfWeek starts the week with Monday, so I'm doing this to make things easier for ordinal-based comparisons
private val dayOfWeekToWeeklyRepeaterDayMap: Map<DayOfWeek, WeeklyRepeater.Day> = mapOf(
    DayOfWeek.SUNDAY to WeeklyRepeater.Day.SUNDAY,
    DayOfWeek.MONDAY to WeeklyRepeater.Day.MONDAY,
    DayOfWeek.TUESDAY to WeeklyRepeater.Day.TUESDAY,
    DayOfWeek.WEDNESDAY to WeeklyRepeater.Day.WEDNESDAY,
    DayOfWeek.THURSDAY to WeeklyRepeater.Day.THURSDAY,
    DayOfWeek.FRIDAY to WeeklyRepeater.Day.FRIDAY,
    DayOfWeek.SATURDAY to WeeklyRepeater.Day.SATURDAY
)

fun DayOfWeek.toWeeklyRepeaterDay(): WeeklyRepeater.Day? = dayOfWeekToWeeklyRepeaterDayMap[this]
