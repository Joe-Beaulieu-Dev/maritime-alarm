package com.joebsource.lavalarm.alarm.data.model

import java.time.LocalDateTime

data class AlarmExecutionData(
    val id: Int,
    val name: String,
    val executionDateTime: LocalDateTime,
    val encodedRepeatingDays: Int,
    val ringtoneUri: String,
    val isVibrationEnabled: Boolean,
    val snoozeDuration: Int
)
