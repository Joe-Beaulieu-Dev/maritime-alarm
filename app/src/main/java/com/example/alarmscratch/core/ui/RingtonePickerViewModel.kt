package com.example.alarmscratch.core.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.toRoute
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.data.repository.RingtoneRepository
import com.example.alarmscratch.core.navigation.CoreNavComponent2
import com.example.alarmscratch.core.navigation.DestinationNavType
import com.example.alarmscratch.core.navigation.RingtonePickerScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.reflect.typeOf

class RingtonePickerViewModel(
    savedStateHandle: SavedStateHandle,
    ringtoneRepository: RingtoneRepository
) : ViewModel() {

    val ringtoneDataList = ringtoneRepository.getAllRingtoneData()
    private val initialRingtoneUri: String = savedStateHandle.toRoute<RingtonePickerScreen>(
        typeMap = mapOf(typeOf<CoreNavComponent2?>() to DestinationNavType.CoreNavComponent2Type)
    ).ringtoneUriString
    private val _selectedRingtoneUri: MutableStateFlow<String> = MutableStateFlow(initialRingtoneUri)
    val selectedRingtoneUri: StateFlow<String> = _selectedRingtoneUri.asStateFlow()

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

    fun selectRingtone(ringtoneUriString: String) {
        _selectedRingtoneUri.value = ringtoneUriString
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
}
