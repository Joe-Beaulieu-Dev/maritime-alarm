package com.example.alarmscratch.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.saveable.mapSaver
import com.example.alarmscratch.R

sealed class Destination(
    val route: String,
    val navComponent: AlarmNavComponent?
) {
    object NavigableScreen : Destination(
        route = "navigable_screen",
        navComponent = null
    )

    object AlarmList : Destination(
        route = "alarm_list",
        navComponent = AlarmNavComponent(
            navNameRes = R.string.nav_alarm,
            navIcon = Icons.Default.Alarm
        )
    )

    object AlarmCreation: Destination(
        route = "alarm_creation",
        navComponent = null
    )

    object Settings : Destination(
        route = "settings",
        navComponent = AlarmNavComponent(
            navNameRes = R.string.nav_settings,
            navIcon = Icons.Default.Settings
        )
    )

    object GeneralSettings : Destination(
        route = "general_settings",
        navComponent = null
    )

    object AlarmDefaultSettings : Destination(
        route = "alarm_default_settings",
        navComponent = null
    )

    companion object {

        val ALL_DESTINATIONS = listOf(
            AlarmList,
            Settings,
            GeneralSettings,
            AlarmDefaultSettings,
            AlarmCreation
        )

        // TODO: Might remove this. Doesn't seem needed anymore, but not removing yet.
        val Saver = run {
            val routeKey = "route"
            mapSaver(
                save = { mapOf(routeKey to it.route) },
                restore = {
                    when (val route = it[routeKey]) {
                        is String ->
                            fromRoute(route)
                        else ->
                            AlarmList
                    }
                }
            )
        }

        private fun fromRoute(route: String): Destination = ALL_DESTINATIONS.firstOrNull { it.route == route } ?: AlarmList
    }
}
