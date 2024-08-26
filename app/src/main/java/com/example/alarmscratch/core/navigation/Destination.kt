package com.example.alarmscratch.core.navigation

import kotlinx.serialization.Serializable

interface Destination

@Serializable
data object CoreScreen : Destination

@Serializable
data object AlarmListScreen : Destination

@Serializable
data object AlarmCreationScreen : Destination

@Serializable
data class AlarmEditScreen(val alarmId: Int) : Destination

@Serializable
data class RingtonePickerScreen(val ringtoneUriString: String) : Destination

@Serializable
data object SettingsScreen : Destination

@Serializable
data object GeneralSettingsScreen : Destination

@Serializable
data object AlarmDefaultsScreen : Destination
