package com.example.alarmscratch.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.alarmscratch.R

class AlarmNavComponent(
    @StringRes
    val navName: Int,
    val navIcon: ImageVector
)

val alarmScreens = listOf(Destination.AlarmList, Destination.AlarmCreation, Destination.AlarmSettings)

sealed class Destination(
    val route: String,
    val navComponent: AlarmNavComponent?
) {
    data object AlarmList : Destination(
        route = "alarm_list",
        navComponent = AlarmNavComponent(
            navName = R.string.nav_alarm,
            navIcon = Icons.Default.Alarm
        )
    )

    data object AlarmSettings : Destination(
        route = "alarm_settings",
        navComponent = AlarmNavComponent(
            navName = R.string.nav_settings,
            navIcon = Icons.Default.Settings
        )
    )

    data object AlarmCreation: Destination(
        route = "alarm_creation",
        navComponent = null
    )
}
