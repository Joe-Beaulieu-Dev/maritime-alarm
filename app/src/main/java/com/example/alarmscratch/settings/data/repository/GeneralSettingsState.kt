package com.example.alarmscratch.settings.data.repository

import com.example.alarmscratch.settings.data.model.GeneralSettings

sealed interface GeneralSettingsState {
    data object Loading : GeneralSettingsState
    data class Success(val generalSettings: GeneralSettings) : GeneralSettingsState
    data class Error(val throwable: Throwable) : GeneralSettingsState
}
