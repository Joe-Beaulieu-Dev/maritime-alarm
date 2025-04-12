package com.joebsource.lavalarm.core.ui.snackbar.global

import com.joebsource.lavalarm.core.ui.snackbar.SnackbarEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object GlobalSnackbarController {

    private val snackbarChannel = Channel<SnackbarEvent>()
    val snackbarFlow = snackbarChannel.receiveAsFlow()

    suspend fun sendEvent(snackbarEvent: SnackbarEvent) {
        try {
            snackbarChannel.send(snackbarEvent)
        } catch (e: Exception) {
            // SendChannel.send() can theoretically throw any type of Exception
            // if called on a Channel that is already closed.
            // Re-throw CancellationException.
            // No functionality beyond this is desired, so all other Exceptions
            // are simply consumed to prevent crashes.
            if (e is CancellationException) {
                throw e
            }
        }
    }
}
