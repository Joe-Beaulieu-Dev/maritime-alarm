package com.octrobi.lavalarm.core.extension

import android.content.BroadcastReceiver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun BroadcastReceiver.doAsync(
    applicationScope: CoroutineScope,
    dispatcher: CoroutineDispatcher,
    block: suspend () -> Unit
) {
    val pendingResult = goAsync()
    applicationScope.launch(dispatcher) {
        try {
            block()
        } finally {
            pendingResult.finish()
        }
    }
}
