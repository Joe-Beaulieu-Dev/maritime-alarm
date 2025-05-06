package com.octrobi.lavalarm.core.ui.core.component

import androidx.compose.ui.graphics.vector.ImageVector

sealed interface AlarmCountdownState {
    data object Loading : AlarmCountdownState
    data class Success(val icon: ImageVector, val countdownText: String) : AlarmCountdownState
}
