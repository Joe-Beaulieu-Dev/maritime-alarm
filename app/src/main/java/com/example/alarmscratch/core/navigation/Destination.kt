package com.example.alarmscratch.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Settings
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.alarmscratch.R

val ALL_DESTINATIONS = listOf(
    NavigableScreen,
    AlarmList,
    AlarmCreation,
    AlarmEdit,
    Settings,
    GeneralSettings,
    AlarmDefaultSettings
)

interface Destination {
    val route: String
    val navComponent: CoreNavComponent?
}

object NavigableScreen : Destination {
    override val route = "navigable_screen"
    override val navComponent = null
}

object AlarmList : Destination {
    override val route = "alarm_list"
    override val navComponent = CoreNavComponent(
        navNameRes = R.string.nav_alarm,
        navIcon = Icons.Default.Alarm
    )
}

object AlarmCreation : Destination {
    override val route = "alarm_creation"
    override val navComponent = null
}

object AlarmEdit : Destination {
    override val route = "alarm_edit"
    override val navComponent = null
    const val alarmIdArg = "alarmIdArg"
    val routeWithArgs = "$route/{$alarmIdArg}"
    val args = listOf(navArgument(alarmIdArg) { type = NavType.IntType })
}

object Settings : Destination {
    override val route = "settings"
    override val navComponent = CoreNavComponent(
        navNameRes = R.string.nav_settings,
        navIcon = Icons.Default.Settings
    )
}

object GeneralSettings : Destination {
    override val route = "general_settings"
    override val navComponent = null
}

object AlarmDefaultSettings : Destination {
    override val route = "alarm_default_settings"
    override val navComponent = null
}
