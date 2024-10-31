package com.example.alarmscratch.alarm.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class AlarmExecutionData(
    val id: Int,
    val name: String,
    val executionDateTime: LocalDateTime,
    val ringtoneUri: String,
    val isVibrationEnabled: Boolean,
    val snoozeDuration: Int
) : Parcelable
