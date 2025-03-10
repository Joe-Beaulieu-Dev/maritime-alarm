package com.example.alarmscratch.core.ui.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SimplePermissionGateViewModel : ViewModel() {

    // Permissions
    private val _isPermissionGranted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted.asStateFlow()

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SimplePermissionGateViewModel()
            }
        }
    }

    /*
     * Check
     */

    fun checkForPermission(context: Context, permission: String) {
        _isPermissionGranted.value =
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
