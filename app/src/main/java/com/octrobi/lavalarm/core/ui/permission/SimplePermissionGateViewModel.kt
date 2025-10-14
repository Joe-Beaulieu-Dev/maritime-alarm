package com.octrobi.lavalarm.core.ui.permission

import android.content.Context
import androidx.lifecycle.ViewModel
import com.octrobi.lavalarm.core.util.PermissionUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SimplePermissionGateViewModel @Inject constructor() : ViewModel() {

    // Permissions
    private val _isPermissionGranted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted.asStateFlow()

    /*
     * Check
     */

    fun checkForPermission(context: Context, permission: Permission) {
        _isPermissionGranted.value = PermissionUtil.isPermissionGranted(context, permission)
    }
}
