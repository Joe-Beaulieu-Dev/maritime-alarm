package com.joebsource.lavalarm.core.ui.notificationcheck

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
fun SimpleNotificationGate(
    appNotificationChannel: AppNotificationChannel,
    gatedComposable: @Composable () -> Unit,
    simpleNotificationGateViewModel: SimpleNotificationGateViewModel = viewModel(factory = SimpleNotificationGateViewModel.Factory)
) {
    // State
    val isNotificationChannelEnabled by simpleNotificationGateViewModel.isNotificationChannelEnabled.collectAsState()

    // Notification Channel check
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val checkNotificationChannelStatus: () -> Unit = {
        simpleNotificationGateViewModel.checkNotificationChannelStatus(context, appNotificationChannel)
    }

    LaunchedEffect(key1 = context, key2 = lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                checkNotificationChannelStatus()
            }
        }
    }

    // Show gated composable if Notification Channel is enabled
    if (isNotificationChannelEnabled) {
        gatedComposable()
    }
}
