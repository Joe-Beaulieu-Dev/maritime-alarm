package com.example.alarmscratch.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.alarmscratch.R

enum class NavComponent(
    @StringRes val navNameRes: Int,
    val navIcon: ImageVector,
    val destination: Destination
) {
    ALARM_LIST(
        navNameRes = R.string.nav_alarm,
        navIcon = Icons.Default.Alarm,
        destination = AlarmListScreen
    ),
    SETTINGS(
        navNameRes = R.string.nav_settings,
        navIcon = Icons.Default.Settings,
        destination = SettingsScreen
    )
}
