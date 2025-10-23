package com.octrobi.lavalarm.alarm.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class AlarmExecutionData(
    val id: Int,
    val name: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val executionDateTime: LocalDateTime,
    val encodedRepeatingDays: Int,
    val ringtoneUri: String,
    val isVibrationEnabled: Boolean,
    val snoozeDuration: Int
)
