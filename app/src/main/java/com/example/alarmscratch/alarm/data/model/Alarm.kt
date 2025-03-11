package com.example.alarmscratch.alarm.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import java.time.LocalDateTime

@Entity(tableName = "alarm")
data class Alarm(
    // TODO: Do @ColumnInfo for custom column names
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val enabled: Boolean = true,
    @ColumnInfo(name = "date_time")
    val dateTime: LocalDateTime = LocalDateTimeUtil.nowTruncated(),
    @ColumnInfo(name = "weekly_repeater")
    val weeklyRepeater: WeeklyRepeater = WeeklyRepeater(),
    @ColumnInfo(name = "ringtone_uri_string")
    val ringtoneUriString: String,
    @ColumnInfo(name = "is_vibration_enabled")
    val isVibrationEnabled: Boolean = false,
    @ColumnInfo(name = "snooze_date_time")
    val snoozeDateTime: LocalDateTime? = null,
    @ColumnInfo(name = "snooze_duration")
    val snoozeDuration: Int
)
