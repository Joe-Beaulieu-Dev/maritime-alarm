package com.octrobi.lavalarm.alarm.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.octrobi.lavalarm.core.extension.LocalDateTimeUtil
import java.time.LocalDateTime

@Entity(tableName = "alarm")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val enabled: Boolean = true,
    @ColumnInfo(name = "date_time")
    val dateTime: LocalDateTime = LocalDateTimeUtil.nowTruncated(),
    @ColumnInfo(name = "weekly_repeater")
    val weeklyRepeater: WeeklyRepeater = WeeklyRepeater(),
    @ColumnInfo(name = "ringtone_uri")
    val ringtoneUri: String,
    @ColumnInfo(name = "is_vibration_enabled")
    val isVibrationEnabled: Boolean = false,
    @ColumnInfo(name = "snooze_date_time")
    val snoozeDateTime: LocalDateTime? = null,
    @ColumnInfo(name = "snooze_duration")
    val snoozeDuration: Int
)
