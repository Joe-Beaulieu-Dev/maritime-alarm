package com.example.alarmscratch.core.ui.ringtonepicker

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.toRoute
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.data.repository.RingtoneRepository
import com.example.alarmscratch.core.navigation.Destination
import com.example.alarmscratch.core.ringtone.RingtonePlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RingtonePickerViewModel(
    savedStateHandle: SavedStateHandle,
    ringtoneRepository: RingtoneRepository
) : ViewModel(), DefaultLifecycleObserver {

    val ringtoneDataList = ringtoneRepository.getAllRingtoneData()
    private val initialRingtoneUri: String = savedStateHandle.toRoute<Destination.RingtonePickerScreen>().ringtoneUriString
    private val _selectedRingtoneUri: MutableStateFlow<String> = MutableStateFlow(initialRingtoneUri)
    val selectedRingtoneUri: StateFlow<String> = _selectedRingtoneUri.asStateFlow()
    private val _isRingtonePlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRingtonePlaying: StateFlow<Boolean> = _isRingtonePlaying.asStateFlow()

    init {
        // Register this ViewModel to the Application's Lifecycle.
        // This is to enable the ability to stop any currently playing Ringtone when the app is
        // placed into the background, by implementing DefaultLifecycleObserver.onStop().
        //
        // According to the ProcessLifecycleObserver documentation, the Application's Lifecycle's ON_STOP Event
        // will not be dispatched if there are Activities being recreated due to a configuration change.
        // Therefore, onStop() will not be called unless the app is getting put into the background.
        //
        // "Why not just stop the Ringtone in ViewModel.onCleared()?" This is because ViewModel.onCleared() isn't called
        // when the app is simply put into the background. However, ViewModel.onCleared() is still used to stop the Ringtone.
        // This is because onStop() won't be called when the ViewModel is destroyed via back-navigation; however, ViewModel.onCleared()
        // will be called in this situation. Therefore, both onStop() and onCleared() are required for proper Ringtone playback management.
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // TODO: Do something about this
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                return RingtonePickerViewModel(
                    savedStateHandle = extras.createSavedStateHandle(),
                    ringtoneRepository = RingtoneRepository(application)
                ) as T
            }
        }
    }

    fun selectRingtone(context: Context, ringtoneUriString: String) {
        // Play or Stop Ringtone
        if (_isRingtonePlaying.value) {
            if (ringtoneUriString == _selectedRingtoneUri.value) {
                stopRingtone()
            } else {
                playRingtone(context, ringtoneUriString)
            }
        } else {
            playRingtone(context, ringtoneUriString)
        }

        // Select Ringtone
        _selectedRingtoneUri.value = ringtoneUriString
    }

    private fun playRingtone(context: Context, ringtoneUri: String) {
        RingtonePlayerManager.startAlarmSound(context, ringtoneUri)
        _isRingtonePlaying.value = true
    }

    private fun stopRingtone() {
        RingtonePlayerManager.stopAlarmSound()
        _isRingtonePlaying.value = false
    }

    /**
     * Saves the selected Ringtone's URI String to the given SavedStateHandle.
     * Use this to pass the URI String back to the previous screen via its NavBackStackEntry.
     *
     * @param savedStateHandle the previous screen's SavedStateHandle
     */
    fun saveRingtone(savedStateHandle: SavedStateHandle?) {
        savedStateHandle?.set(RingtoneData.KEY_FULL_RINGTONE_URI_STRING, _selectedRingtoneUri.value)
    }

    /*
     ******************************
     **** Lifecycle Management ****
     ******************************
     */

    /**
     * Stop Ringtone playback when the app is put into the background
     *
     * @param owner the LifecycleOwner whose state is changing
     */
    override fun onStop(owner: LifecycleOwner) {
        stopRingtone()
        super.onStop(owner)
    }

    /**
     * Stop Ringtone playback when the ViewModel is destroyed.
     * onCleared() will not necessarily be called when the app is simply placed into the background.
     */
    override fun onCleared() {
        // If you don't call removeObserver(), then the ViewModel will leak
        // and you'll get ghost calls to onStop() for the rest of the Application's Lifecycle.
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        stopRingtone()
        super.onCleared()
    }
}
