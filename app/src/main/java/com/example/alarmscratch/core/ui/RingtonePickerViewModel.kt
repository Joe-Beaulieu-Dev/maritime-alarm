package com.example.alarmscratch.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.core.data.repository.RingtoneRepository

class RingtonePickerViewModel(ringtoneRepository: RingtoneRepository) : ViewModel() {

    val ringtoneList = ringtoneRepository.getAllRingtones()

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
}
