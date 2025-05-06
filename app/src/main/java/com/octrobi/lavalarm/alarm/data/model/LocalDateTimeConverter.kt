package com.octrobi.lavalarm.alarm.data.model

import androidx.room.TypeConverter
import java.time.LocalDateTime

class LocalDateTimeConverter {

    // TODO: Handle java exception
    @TypeConverter
    fun toLocalDateTime(string: String): LocalDateTime = LocalDateTime.parse(string)

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime): String = localDateTime.toString()
}
