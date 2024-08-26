package com.example.alarmscratch.core.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.alarmscratch.R

enum class NavComponent(
    @StringRes val navNameRes: Int,
    @DrawableRes val navIconRes: Int,
    val destination: Destination
) {
    ALARM_LIST_NAV_COMPONENT(
        navNameRes = R.string.nav_alarm,
        navIconRes = R.drawable.ic_alarm_24dp,
        destination = AlarmListScreen
    ),
    SETTINGS_NAV_COMPONENT(
        navNameRes = R.string.nav_settings,
        navIconRes = R.drawable.ic_settings_24dp,
        destination = SettingsScreen
    )
}
