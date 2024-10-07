package com.example.alarmscratch.settings.ui.generalsettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.settings.data.model.GeneralSettings
import com.example.alarmscratch.settings.data.model.TimeDisplay
import com.example.alarmscratch.settings.data.repository.GeneralSettingsRepository
import com.example.alarmscratch.settings.data.repository.GeneralSettingsState
import com.example.alarmscratch.settings.data.repository.generalSettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class GeneralSettingsViewModel(private val generalSettingsRepository: GeneralSettingsRepository) : ViewModel() {

    private val _modifiedGeneralSettings: MutableStateFlow<GeneralSettingsState> = MutableStateFlow(GeneralSettingsState.Loading)
    val modifiedGeneralSettings: StateFlow<GeneralSettingsState> = _modifiedGeneralSettings.asStateFlow()

    init {
        viewModelScope.launch {
            generalSettingsRepository.generalSettingsFlow
                .map<GeneralSettings, GeneralSettingsState> { generalSettings -> GeneralSettingsState.Success(generalSettings) }
                .catch { throwable -> emit(GeneralSettingsState.Error(throwable)) }
                .collect { generalSettingsState -> _modifiedGeneralSettings.value = generalSettingsState }
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // TODO: Do something about this
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                return GeneralSettingsViewModel(
                    generalSettingsRepository = GeneralSettingsRepository(application.generalSettingsDataStore)
                ) as T
            }
        }
    }

    suspend fun saveGeneralSettings() {
        if (_modifiedGeneralSettings.value is GeneralSettingsState.Success) {
            val generalSettings = (_modifiedGeneralSettings.value as GeneralSettingsState.Success).generalSettings
            generalSettingsRepository.updateGeneralSettings(generalSettings)
        }
    }

    fun updateTimeDisplay(timeDisplay: TimeDisplay) {
        if (_modifiedGeneralSettings.value is GeneralSettingsState.Success) {
            val generalSettings = (_modifiedGeneralSettings.value as GeneralSettingsState.Success).generalSettings
            _modifiedGeneralSettings.value = GeneralSettingsState.Success(generalSettings.copy(timeDisplay = timeDisplay))
        }
    }
}
