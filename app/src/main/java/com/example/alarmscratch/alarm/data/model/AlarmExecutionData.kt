package com.example.alarmscratch.alarm.data.model

import java.time.LocalDateTime

data class AlarmExecutionData(
    val id: Int,
    val name: String,
    val executionDateTime: LocalDateTime,
    val ringtoneUri: String,
    val isVibrationEnabled: Boolean,
    val snoozeDuration: Int
)
