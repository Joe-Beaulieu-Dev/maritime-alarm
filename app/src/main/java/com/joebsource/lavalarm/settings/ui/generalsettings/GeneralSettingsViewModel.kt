package com.joebsource.lavalarm.settings.ui.generalsettings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.joebsource.lavalarm.settings.data.model.GeneralSettings
import com.joebsource.lavalarm.settings.data.model.TimeDisplay
import com.joebsource.lavalarm.settings.data.repository.GeneralSettingsRepository
import com.joebsource.lavalarm.settings.data.repository.GeneralSettingsState
import com.joebsource.lavalarm.settings.data.repository.generalSettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class GeneralSettingsViewModel(private val generalSettingsRepository: GeneralSettingsRepository) : ViewModel() {

    // General Settings
    private val referenceGeneralSettings: MutableStateFlow<GeneralSettingsState> = MutableStateFlow(GeneralSettingsState.Loading)
    private val _modifiedGeneralSettings: MutableStateFlow<GeneralSettingsState> = MutableStateFlow(GeneralSettingsState.Loading)
    val modifiedGeneralSettings: StateFlow<GeneralSettingsState> = _modifiedGeneralSettings.asStateFlow()

    // Dialog
    private val _showUnsavedChangesDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showUnsavedChangesDialog: StateFlow<Boolean> = _showUnsavedChangesDialog.asStateFlow()

    init {
        viewModelScope.launch {
            generalSettingsRepository.generalSettingsFlow
                .map<GeneralSettings, GeneralSettingsState> { generalSettings -> GeneralSettingsState.Success(generalSettings) }
                .catch { throwable -> emit(GeneralSettingsState.Error(throwable)) }
                .collect { generalSettingsState ->
                    referenceGeneralSettings.value = generalSettingsState
                    _modifiedGeneralSettings.value = generalSettingsState
                }
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)

                GeneralSettingsViewModel(
                    generalSettingsRepository = GeneralSettingsRepository(application.generalSettingsDataStore)
                )
            }
        }
    }

    /*
     * Save
     */

    fun saveGeneralSettings() {
        if (_modifiedGeneralSettings.value is GeneralSettingsState.Success) {
            val generalSettings = (_modifiedGeneralSettings.value as GeneralSettingsState.Success).generalSettings
            viewModelScope.launch {
                generalSettingsRepository.updateGeneralSettings(generalSettings)
            }
        }
    }

    fun updateTimeDisplay(timeDisplay: TimeDisplay) {
        if (_modifiedGeneralSettings.value is GeneralSettingsState.Success) {
            val generalSettings = (_modifiedGeneralSettings.value as GeneralSettingsState.Success).generalSettings
            _modifiedGeneralSettings.value = GeneralSettingsState.Success(generalSettings.copy(timeDisplay = timeDisplay))
        }
    }

    /*
     * Navigation
     */

    fun tryNavigateUp(navHostController: NavHostController) {
        if (
            referenceGeneralSettings.value is GeneralSettingsState.Success &&
            _modifiedGeneralSettings.value is GeneralSettingsState.Success
        ) {
            if (hasUnsavedChanges()) {
                _showUnsavedChangesDialog.value = true
            } else {
                navHostController.navigateUp()
            }
        }
    }

    fun tryNavigateBack(navHostController: NavHostController) {
        if (
            referenceGeneralSettings.value is GeneralSettingsState.Success &&
            _modifiedGeneralSettings.value is GeneralSettingsState.Success
        ) {
            if (hasUnsavedChanges()) {
                _showUnsavedChangesDialog.value = true
            } else {
                navHostController.popBackStack()
            }
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
        if (
            referenceGeneralSettings.value is GeneralSettingsState.Success &&
            _modifiedGeneralSettings.value is GeneralSettingsState.Success
        ) {
            val refGeneralSettings = (referenceGeneralSettings.value as GeneralSettingsState.Success).generalSettings
            val generalSettings = (_modifiedGeneralSettings.value as GeneralSettingsState.Success).generalSettings
            refGeneralSettings != generalSettings
        } else {
            false
        }
}
