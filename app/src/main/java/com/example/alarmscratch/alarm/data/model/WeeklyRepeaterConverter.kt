package com.example.alarmscratch.alarm.data.model

import androidx.room.TypeConverter

class WeeklyRepeaterConverter {

    @TypeConverter
    fun toWeeklyRepeater(encodedRepeatingDays: Int): WeeklyRepeater = WeeklyRepeater(encodedRepeatingDays = encodedRepeatingDays)

    @TypeConverter
    fun fromWeeklyRepeater(weeklyRepeater: WeeklyRepeater): Int = weeklyRepeater.getEncodedRepeatingDays()
}
