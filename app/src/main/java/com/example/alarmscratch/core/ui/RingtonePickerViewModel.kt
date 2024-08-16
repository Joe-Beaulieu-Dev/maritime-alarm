package com.example.alarmscratch.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.core.data.repository.RingtoneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RingtonePickerViewModel(ringtoneRepository: RingtoneRepository) : ViewModel() {

    val ringtoneList = ringtoneRepository.getAllRingtones()
    private val _selectedRingtoneId: MutableStateFlow<Int> = MutableStateFlow(DEFAULT_RINGTONE_ID)
    val selectedRingtoneId: StateFlow<Int> = _selectedRingtoneId.asStateFlow()

    companion object {

        private const val DEFAULT_RINGTONE_ID = -1

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // TODO: Do something about this
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                return RingtonePickerViewModel(ringtoneRepository = RingtoneRepository(application)) as T
            }
        }
    }

    fun selectRingtone(ringtoneId: Int) {
        _selectedRingtoneId.value = ringtoneId
    }
}
