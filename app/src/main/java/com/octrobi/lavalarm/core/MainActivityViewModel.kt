package com.octrobi.lavalarm.core

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.octrobi.lavalarm.alarm.data.repository.AlarmDatabase
import com.octrobi.lavalarm.alarm.data.repository.AlarmRepository
import com.octrobi.lavalarm.core.recovery.ForceStopRecoveryHandler
import com.octrobi.lavalarm.core.recovery.ForceStopRecoveryState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val shouldPerformForceStopRecovery: StateFlow<ForceStopRecoveryState> =
        savedStateHandle.getStateFlow(
            KEY_SHOULD_PERFORM_FORCE_STOP_RECOVERY,
            ForceStopRecoveryState.Unchecked
        )

    companion object {

        // SavedStateHandle Keys
        private const val KEY_SHOULD_PERFORM_FORCE_STOP_RECOVERY = "should_perform_force_stop_recovery"

        fun provideFactory(): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    MainActivityViewModel(savedStateHandle = createSavedStateHandle())
                }
            }
    }

    fun checkForceStopPreApi35(context: Context) {
        if (shouldPerformForceStopRecovery.value is ForceStopRecoveryState.Unchecked) {
            viewModelScope.launch {
                val alarmRepository = AlarmRepository(
                    AlarmDatabase
                        .getDatabase(context.createDeviceProtectedStorageContext())
                        .alarmDao()
                )

                val shouldPerformRecovery =
                    ForceStopRecoveryHandler.shouldPerformForceStopRecoveryPreApi35(context, alarmRepository)

                savedStateHandle[KEY_SHOULD_PERFORM_FORCE_STOP_RECOVERY] =
                    if (shouldPerformRecovery) {
                        ForceStopRecoveryState.ShouldPerformRecovery
                    } else {
                        ForceStopRecoveryState.ShouldNotPerformRecovery
                    }
            }
        }
    }

    fun performForceStopRecoveryPreApi35(context: Context) {
        // Perform Force Stop Recovery
        ForceStopRecoveryHandler.performForceStopRecoveryPreApi35(context)

        // Prevent multiple recoveries
        savedStateHandle[KEY_SHOULD_PERFORM_FORCE_STOP_RECOVERY] = ForceStopRecoveryState.RecoveryPerformed
    }
}
