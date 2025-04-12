package com.joebsource.lavalarm.core.util

import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.joebsource.lavalarm.core.ui.permission.Permission

object PermissionUtil {

    fun isPermissionGranted(context: Context, permission: Permission): Boolean =
        if (permission.permissionType == Permission.PermissionType.SPECIAL) {
            // Special Permissions require usage of their own unique permission check functions.
            // Calling ContextCompat.checkSelPermission() on a Special Permission will always return true.
            when (permission) {
                is Permission.ScheduleExactAlarm ->
                    context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
                else ->
                    false
            }
        } else {
            ContextCompat.checkSelfPermission(
                context,
                permission.permissionString
            ) == PackageManager.PERMISSION_GRANTED
        }
}
