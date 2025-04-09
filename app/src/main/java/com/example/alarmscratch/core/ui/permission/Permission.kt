package com.example.alarmscratch.core.ui.permission

import android.Manifest
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import com.example.alarmscratch.R

sealed class Permission(
    val permissionString: String,
    @StringRes val systemDialogBodyRes: Int,
    @StringRes val systemSettingsBodyRes: Int,
    val systemSettingsAction: String
) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    data object PostNotifications : Permission(
        permissionString = Manifest.permission.POST_NOTIFICATIONS,
        systemDialogBodyRes = R.string.permission_missing_notifications_system_dialog,
        systemSettingsBodyRes = R.string.permission_missing_notifications_system_settings,
        systemSettingsAction = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    )

    data object ScheduleExactAlarm : Permission(
        permissionString = Manifest.permission.SCHEDULE_EXACT_ALARM,
        systemDialogBodyRes = R.string.permission_missing_alarm_system_settings,
        systemSettingsBodyRes = R.string.permission_missing_alarm_system_settings,
        systemSettingsAction = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
    )
}
