package com.example.alarmscratch.core.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.data.repository.RingtoneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RingtonePickerViewModel(ringtoneRepository: RingtoneRepository) : ViewModel() {

    val ringtoneDataList = ringtoneRepository.getAllRingtoneData()
    private val _selectedRingtone: MutableStateFlow<RingtoneData> = MutableStateFlow(RingtoneData(-1, "", ""))
    val selectedRingtone: StateFlow<RingtoneData> = _selectedRingtone.asStateFlow()

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // TODO: Do something about this
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                return RingtonePickerViewModel(ringtoneRepository = RingtoneRepository(application)) as T
            }
        }
    }

    fun selectRingtone(ringtoneData: RingtoneData) {
        _selectedRingtone.value = ringtoneData
    }

    /**
     * Saves the selected Ringtone's URI String to the given SavedStateHandle.
     * Use this to pass the URI String back to the previous screen via its NavBackStackEntry.
     *
     * @param savedStateHandle the previous screen's SavedStateHandle
     */
    fun saveRingtone(savedStateHandle: SavedStateHandle?) {
        val ringtoneUriString = _selectedRingtone.value.getFullUriString()
        savedStateHandle?.set(RingtoneData.FULL_RINGTONE_URI, ringtoneUriString)
    }
}
