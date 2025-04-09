package com.example.alarmscratch.core.util

import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.alarmscratch.core.ui.permission.Permission

object PermissionUtil {

    fun isPermissionGranted(context: Context, permission: Permission): Boolean =
        if (permission is Permission.ScheduleExactAlarm) {
            // SCHEDULE_EXACT_ALARM is a Special Permission and therefore requires usage
            // of its own unique permission check function. If you call ContextCompat.checkSelPermission()
            // with SCHEDULE_EXACT_ALARM it will always return true.
            context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
        } else {
            ContextCompat.checkSelfPermission(
                context,
                permission.permissionString
            ) == PackageManager.PERMISSION_GRANTED
        }
}
