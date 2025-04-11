package com.joebsource.lavalarm.core.ui.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joebsource.lavalarm.core.ui.snackbar.SnackbarEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CoreScreenViewModel : ViewModel() {

    // Snackbar
    private val localSnackbarChannel = Channel<SnackbarEvent>()
    val localSnackbarFlow = localSnackbarChannel.receiveAsFlow()

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CoreScreenViewModel()
            }
        }
    }

    fun retrieveSnackbarFromPrevious(message: String?) {
        if (message != null) {
            viewModelScope.launch {
                try {
                    localSnackbarChannel.send(SnackbarEvent(message))
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
    }
}
