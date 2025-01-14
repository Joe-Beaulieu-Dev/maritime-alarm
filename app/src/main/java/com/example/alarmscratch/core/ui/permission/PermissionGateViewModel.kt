package com.example.alarmscratch.core.ui.permission

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionGateViewModel : ViewModel() {

    // Permissions
    private val _attemptedToAskForPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val attemptedToAskForPermission: StateFlow<Boolean> = _attemptedToAskForPermission.asStateFlow()
    val deniedPermissionList = mutableStateListOf<String>()

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
                PermissionGateViewModel() as T
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
}
