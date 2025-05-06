package com.octrobi.lavalarm.core.ui.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.octrobi.lavalarm.core.navigation.Destination
import com.octrobi.lavalarm.core.ui.snackbar.SnackbarEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CoreScreenViewModel : ViewModel() {

    // Navigation Tracking
    private val _currentCoreDestination: MutableStateFlow<Destination> = MutableStateFlow(Destination.AlarmListScreen)
    val currentCoreDestination: StateFlow<Destination> = _currentCoreDestination.asStateFlow()
    private val _previousCoreDestination: MutableStateFlow<Destination> = MutableStateFlow(Destination.AlarmListScreen)
    val previousCoreDestination: StateFlow<Destination> = _previousCoreDestination.asStateFlow()

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

    /*
     * Navigation Tracking
     */

    fun setCurrentCoreDestination(destination: Destination) {
        if (_currentCoreDestination.value != destination) {
            _currentCoreDestination.value = destination
        }
    }

    fun setPreviousCoreDestination(destination: Destination) {
        if (_previousCoreDestination.value != destination) {
            _previousCoreDestination.value = destination
        }
    }

    /*
     * Snackbar
     */

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
