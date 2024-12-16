package com.example.alarmscratch.core.ui.snackbar

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object GlobalSnackbarController {

    private val _eventChannel = Channel<SnackbarEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        try {
            _eventChannel.send(event)
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
