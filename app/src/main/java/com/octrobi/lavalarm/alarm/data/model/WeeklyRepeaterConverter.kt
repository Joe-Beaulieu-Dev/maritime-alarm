package com.octrobi.lavalarm.alarm.data.model

import androidx.room.TypeConverter

class WeeklyRepeaterConverter {

    @TypeConverter
    fun toWeeklyRepeater(encodedRepeatingDays: Int): WeeklyRepeater =
        WeeklyRepeater(encodedRepeatingDays)

    @TypeConverter
    fun fromWeeklyRepeater(weeklyRepeater: WeeklyRepeater): Int =
        weeklyRepeater.toEncodedRepeatingDays()
}
