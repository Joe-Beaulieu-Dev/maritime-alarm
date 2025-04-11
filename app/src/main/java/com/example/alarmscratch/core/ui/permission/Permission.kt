package com.example.alarmscratch.core.ui.permission

import android.Manifest
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import com.example.alarmscratch.R

sealed class Permission(
    val permissionString: String,
    val permissionType: PermissionType,
    @StringRes val systemDialogBodyRes: Int,
    @StringRes val systemSettingsBodyRes: Int,
    val systemSettingsAction: String
) {

    enum class PermissionType {
        STANDARD_RUNTIME,
        SPECIAL
    }

    /*
     * Standard Runtime Permissions
     */

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    data object PostNotifications : Permission(
        permissionString = Manifest.permission.POST_NOTIFICATIONS,
        permissionType = PermissionType.STANDARD_RUNTIME,
        systemDialogBodyRes = R.string.permission_missing_notifications_system_dialog,
        systemSettingsBodyRes = R.string.permission_missing_notifications_system_settings,
        systemSettingsAction = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    )

    /*
     * Special Permissions
     */

    data object ScheduleExactAlarm : Permission(
        permissionString = Manifest.permission.SCHEDULE_EXACT_ALARM,
        permissionType = PermissionType.SPECIAL,
        systemDialogBodyRes = R.string.permission_missing_alarm_system_settings,
        systemSettingsBodyRes = R.string.permission_missing_alarm_system_settings,
        systemSettingsAction = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
    )
}
