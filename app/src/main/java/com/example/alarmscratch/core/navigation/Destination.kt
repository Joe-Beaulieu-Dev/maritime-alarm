package com.example.alarmscratch.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Settings
import com.example.alarmscratch.R
import kotlinx.serialization.Serializable

val ALL_DESTINATIONS = listOf(
    CoreScreen,
    AlarmListScreen,
    SettingsScreen,
    GeneralSettingsScreen,
    AlarmDefaultsScreen
)

interface Destination {
    val route: String
    val navComponent: CoreNavComponent?
}

object CoreScreen : Destination {
    override val route = "core_screen"
    override val navComponent = null
}

object AlarmListScreen : Destination {
    override val route = "alarm_list_screen"
    override val navComponent = CoreNavComponent(
        navNameRes = R.string.nav_alarm,
        navIcon = Icons.Default.Alarm
    )
}

// TODO: Put in ALL_DESTINATIONS
@Serializable
object AlarmCreationScreen

// TODO: Put in ALL_DESTINATIONS
@Serializable
data class AlarmEditScreen(val alarmId: Int)

// TODO: Put in ALL_DESTINATIONS
@Serializable
data class RingtonePickerScreen(val ringtoneUriString: String)

object SettingsScreen : Destination {
    override val route = "settings_screen"
    override val navComponent = CoreNavComponent(
        navNameRes = R.string.nav_settings,
        navIcon = Icons.Default.Settings
    )
}

object GeneralSettingsScreen : Destination {
    override val route = "general_settings_screen"
    override val navComponent = null
}

object AlarmDefaultsScreen : Destination {
    override val route = "alarm_defaults_screen"
    override val navComponent = null
}
