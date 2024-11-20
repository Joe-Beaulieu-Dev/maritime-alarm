package com.example.alarmscratch.core.ui.core.component

import androidx.compose.ui.graphics.vector.ImageVector

sealed interface AlarmCloudState {
    data object Loading : AlarmCloudState
    data class Success(val icon: ImageVector, val countdownText: String) : AlarmCloudState
}
