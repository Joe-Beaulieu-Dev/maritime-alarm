package com.example.alarmscratch.core.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.alarmscratch.core.ui.permission.Permission

object PermissionUtil {

    fun isPermissionGranted(context: Context, permission: Permission): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            permission.permissionString
        ) == PackageManager.PERMISSION_GRANTED
}
