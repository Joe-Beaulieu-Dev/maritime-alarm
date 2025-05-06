package com.octrobi.lavalarm.settings.data.repository

import com.octrobi.lavalarm.settings.data.model.GeneralSettings

sealed interface GeneralSettingsState {
    data object Loading : GeneralSettingsState
    data class Success(val generalSettings: GeneralSettings) : GeneralSettingsState
    data class Error(val throwable: Throwable) : GeneralSettingsState
}
