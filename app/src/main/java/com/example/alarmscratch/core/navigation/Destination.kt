package com.example.alarmscratch.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Settings
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.alarmscratch.R

val ALL_DESTINATIONS = listOf(
    CoreScreen,
    AlarmListScreen,
    AlarmCreationScreen,
    AlarmEditScreen,
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

object AlarmCreationScreen : Destination {
    override val route = "alarm_creation_screen"
    override val navComponent = null
}

object AlarmEditScreen : Destination {
    override val route = "alarm_edit_screen"
    override val navComponent = null
    const val alarmIdArg = "alarmIdArg"
    val routeWithArgs = "$route/{$alarmIdArg}"
    val args = listOf(navArgument(alarmIdArg) { type = NavType.IntType })
}

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
