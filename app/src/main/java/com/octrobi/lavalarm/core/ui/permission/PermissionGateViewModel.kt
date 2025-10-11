package com.octrobi.lavalarm.core.ui.permission

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.octrobi.lavalarm.core.util.PermissionUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PermissionGateViewModel @Inject constructor() : ViewModel() {

    // Permissions
    private val _attemptedToAskForPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val attemptedToAskForPermission: StateFlow<Boolean> = _attemptedToAskForPermission.asStateFlow()
    val deniedPermissionList = mutableStateListOf<Permission>()

    /*
     * Callback
     */

    fun onPermissionResult(permission: Permission, isGranted: Boolean) {
        _attemptedToAskForPermission.value = true

        if (!isGranted) {
            deniedPermissionList.add(permission)
        } else if (deniedPermissionList.isNotEmpty()) {
            // List can contain duplicates, remove all instances
            deniedPermissionList.removeAll { it.permissionString == permission.permissionString }
        }
    }

    fun onReturnFromSystemSettings(context: Context, permission: Permission) {
        _attemptedToAskForPermission.value = true

        val isGranted = PermissionUtil.isPermissionGranted(context, permission)
        if (isGranted && deniedPermissionList.isNotEmpty()) {
            // List can contain duplicates, remove all instances
            deniedPermissionList.removeAll { it.permissionString == permission.permissionString }
        }
    }
}
