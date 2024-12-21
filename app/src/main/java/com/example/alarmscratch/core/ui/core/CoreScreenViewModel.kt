package com.example.alarmscratch.core.ui.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.core.ui.snackbar.SnackbarEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CoreScreenViewModel : ViewModel() {

    // Snackbar
    private val snackbarChannel = Channel<SnackbarEvent>()
    val snackbarFlow = snackbarChannel.receiveAsFlow()

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
                CoreScreenViewModel() as T
        }
    }

    fun updateSnackbar(message: String?) {
        if (message != null) {
            viewModelScope.launch {
                try {
                    snackbarChannel.send(SnackbarEvent(message))
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
