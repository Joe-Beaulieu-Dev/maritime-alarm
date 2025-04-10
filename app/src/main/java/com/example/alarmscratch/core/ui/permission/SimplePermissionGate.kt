package com.example.alarmscratch.core.ui.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SimplePermissionGate(
    permission: Permission,
    gatedComposable: @Composable () -> Unit,
    simplePermissionGateViewModel: SimplePermissionGateViewModel = viewModel(factory = SimplePermissionGateViewModel.Factory)
) {
    // State
    val isPermissionGranted by simplePermissionGateViewModel.isPermissionGranted.collectAsState()

    // Permission check
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val checkForPermission: () -> Unit = {
        simplePermissionGateViewModel.checkForPermission(context, permission)
    }

    // Manually refresh the state every time the LifecycleOwner enters the RESUMED state to ensure
    // that we're always up to date with the state of the permission. Since we're using RESUMED here,
    // this will re-trigger when the System Permission Dialog is closed, if it was present.
    LaunchedEffect(key1 = context, key2 = lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            withContext(Dispatchers.Main.immediate) {
                checkForPermission()
            }
        }
    }

    // Show gated composable if permission is granted
    if (isPermissionGranted) {
        gatedComposable()
    }
}
