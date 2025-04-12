package com.joebsource.lavalarm.core.ui.ringtonepicker

import android.app.Application
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.joebsource.lavalarm.core.data.model.RingtoneData
import com.joebsource.lavalarm.core.data.repository.RingtoneRepository
import com.joebsource.lavalarm.core.navigation.Destination
import com.joebsource.lavalarm.core.ringtone.RingtonePlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RingtonePickerViewModel(
    savedStateHandle: SavedStateHandle,
    ringtoneRepository: RingtoneRepository
) : ViewModel(), DefaultLifecycleObserver {

    // Ringtone
    val ringtoneDataList = ringtoneRepository.getAllRingtoneData()
    private val initialRingtoneUri: String = savedStateHandle.toRoute<Destination.RingtonePickerScreen>().ringtoneUri
    private val _selectedRingtoneUri: MutableStateFlow<String> = MutableStateFlow(initialRingtoneUri)
    val selectedRingtoneUri: StateFlow<String> = _selectedRingtoneUri.asStateFlow()

    // Playback
    private val _isRingtonePlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRingtonePlaying: StateFlow<Boolean> = _isRingtonePlaying.asStateFlow()

    // Dialog
    private val _showUnsavedChangesDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showUnsavedChangesDialog: StateFlow<Boolean> = _showUnsavedChangesDialog.asStateFlow()

    init {
        // Register this ViewModel to the Application's Lifecycle.
        // This is to enable the ability to stop any currently playing Ringtone when the app is
        // placed into the background, by implementing DefaultLifecycleObserver.onStop().
        //
        // According to the ProcessLifecycleOwner documentation, the Application's Lifecycle's ON_STOP Event
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

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)

                RingtonePickerViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    ringtoneRepository = RingtoneRepository(application)
                )
            }
        }
    }

    /*
     * Save and Select
     */

    /**
     * Sends the selected Ringtone URI to the previous screen by saving it to the
     * SavedStateHandle associated with its NavBackStackEntry, then navigates back
     * by calling NavHostController.popBackStack().
     *
     * @param navHostController used to pass the Ringtone URI to the previous screen and navigate back
     */
    fun saveRingtone(navHostController: NavHostController) {
        // Send Ringtone URI to previous screen
        navHostController.previousBackStackEntry?.savedStateHandle
            ?.set(RingtoneData.KEY_FULL_RINGTONE_URI, _selectedRingtoneUri.value)

        // Navigate back
        navHostController.popBackStack()
    }

    fun selectRingtone(context: Context, ringtoneUri: String) {
        // Play or Stop Ringtone
        if (_isRingtonePlaying.value) {
            if (ringtoneUri == _selectedRingtoneUri.value) {
                stopRingtone()
            } else {
                playRingtone(context, ringtoneUri)
            }
        } else {
            playRingtone(context, ringtoneUri)
        }

        // Select Ringtone
        _selectedRingtoneUri.value = ringtoneUri
    }

    /*
     * Playback
     */

    private fun playRingtone(context: Context, ringtoneUri: String) {
        RingtonePlayerManager.startAlarmSound(context, ringtoneUri)
        _isRingtonePlaying.value = true
    }

    private fun stopRingtone() {
        RingtonePlayerManager.stopAlarmSound()
        _isRingtonePlaying.value = false
    }

    /*
     * Navigation
     */

    fun tryNavigateUp(navHostController: NavHostController) {
        if (hasUnsavedChanges()) {
            _showUnsavedChangesDialog.value = true
        } else {
            navHostController.navigateUp()
        }
    }

    fun tryNavigateBack(navHostController: NavHostController) {
        if (hasUnsavedChanges()) {
            _showUnsavedChangesDialog.value = true
        } else {
            navHostController.popBackStack()
        }
    }

    /*
     * Dialog
     */

    fun unsavedChangesLeave(navHostController: NavHostController) {
        _showUnsavedChangesDialog.value = false
        // Doesn't code for navHostController.navigateUp(), but there's no
        // third party deeplinking into this app so it's fine.
        navHostController.popBackStack()
    }

    fun unsavedChangesStay() {
        _showUnsavedChangesDialog.value = false
    }

    /*
     * Validation
     */

    private fun hasUnsavedChanges(): Boolean =
        initialRingtoneUri != _selectedRingtoneUri.value

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
