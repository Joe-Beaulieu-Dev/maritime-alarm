package com.example.alarmscratch.core.ui.permission

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import com.example.alarmscratch.R

sealed class Permission(
    val permissionString: String,
    @StringRes val systemDialogBodyRes: Int,
    @StringRes val systemSettingsBodyRes: Int
) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    data object PostNotifications : Permission(
        permissionString = Manifest.permission.POST_NOTIFICATIONS,
        systemDialogBodyRes = R.string.permission_missing_notifications_system_dialog,
        systemSettingsBodyRes = R.string.permission_missing_notifications_system_settings
    )
}
