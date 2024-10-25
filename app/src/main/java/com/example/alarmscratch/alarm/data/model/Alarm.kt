package com.example.alarmscratch.alarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import java.time.LocalDateTime

@Entity(tableName = "alarms")
data class Alarm(
    // TODO: Do @ColumnInfo for custom column names
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val enabled: Boolean = true,
    val dateTime: LocalDateTime = LocalDateTimeUtil.nowTruncated(),
    val weeklyRepeater: WeeklyRepeater = WeeklyRepeater(),
    val ringtoneUriString: String,
    val isVibrationEnabled: Boolean = false,
    val snoozeDateTime: LocalDateTime? = null,
    val snoozeDuration: Int
)
