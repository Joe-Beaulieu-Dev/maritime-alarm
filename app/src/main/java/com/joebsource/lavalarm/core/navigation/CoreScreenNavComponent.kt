package com.joebsource.lavalarm.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import com.joebsource.lavalarm.R

enum class CoreScreenNavComponent(
    @StringRes val navNameRes: Int,
    val navIcon: ImageVector,
    val destination: Destination,
    @StringRes val contentDescriptionRes: Int
) {
    ALARM_LIST(
        navNameRes = R.string.nav_alarm,
        navIcon = Icons.Default.Alarm,
        destination = Destination.AlarmListScreen,
        contentDescriptionRes = R.string.nav_alarm_cd
    ),
    SETTINGS(
        navNameRes = R.string.nav_settings,
        navIcon = Icons.Default.Settings,
        destination = Destination.SettingsScreen,
        contentDescriptionRes = R.string.nav_settings_cd
    );

    companion object {

        fun fromNavBackStackEntry(navBackStackEntry: NavBackStackEntry?): Destination =
            entries.find { navComponent ->
                navBackStackEntry?.destination?.hasRoute(navComponent.destination::class) ?: false
            }?.destination ?: ALARM_LIST.destination
    }
}
