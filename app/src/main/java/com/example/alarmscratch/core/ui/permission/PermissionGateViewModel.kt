package com.example.alarmscratch.core.ui.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionGateViewModel : ViewModel() {

    // Permissions
    private val _attemptedToAskForPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val attemptedToAskForPermission: StateFlow<Boolean> = _attemptedToAskForPermission.asStateFlow()
    val deniedPermissionList = mutableStateListOf<String>()

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PermissionGateViewModel()
            }
        }
    }

    /*
     * Callback
     */

    fun onPermissionResult(permission: String, isGranted: Boolean) {
        _attemptedToAskForPermission.value = true

        if (!isGranted) {
            deniedPermissionList.add(permission)
        } else if (deniedPermissionList.isNotEmpty()) {
            // List can contain duplicates, remove all instances
            deniedPermissionList.removeAll { it == permission }
        }
    }

    fun onReturnFromSystemSettings(context: Context, permission: String) {
        val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        if (isGranted && deniedPermissionList.isNotEmpty()) {
            // List can contain duplicates, remove all instances
            deniedPermissionList.removeAll { it == permission }
        }
    }
}
