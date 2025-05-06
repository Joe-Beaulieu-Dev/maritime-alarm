package com.octrobi.lavalarm.settings.data.model

data class AlarmDefaults(
    val ringtoneUri: String,
    val isVibrationEnabled: Boolean,
    val snoozeDuration: Int
)
