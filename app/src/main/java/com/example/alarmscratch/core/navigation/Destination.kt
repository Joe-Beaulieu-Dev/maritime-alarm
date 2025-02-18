package com.example.alarmscratch.core.navigation

import com.example.alarmscratch.alarm.ui.fullscreenalert.FullScreenAlarmButton
import kotlinx.serialization.Serializable

sealed interface Destination {

    /*
     * Main Screens
     */

    @Serializable
    data object CoreScreen : Destination

    @Serializable
    data object AlarmListScreen : Destination

    @Serializable
    data object SettingsScreen : Destination

    /*
     * Secondary Screens
     */

    @Serializable
    data object AlarmCreationScreen : Destination

    @Serializable
    data class AlarmEditScreen(val alarmId: Int) : Destination

    @Serializable
    data class RingtonePickerScreen(val ringtoneUriString: String) : Destination

    @Serializable
    data object GeneralSettingsScreen : Destination

    @Serializable
    data object AlarmDefaultsScreen : Destination

    /*
     * Alarm Execution Screens
     */

    @Serializable
    data object FullScreenAlarmScreen : Destination

    @Serializable
    data class PostAlarmConfirmationScreen(
        val fullScreenAlarmButton: FullScreenAlarmButton,
        val snoozeDuration: Int
    ) : Destination
}
