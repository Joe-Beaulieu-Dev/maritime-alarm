package com.octrobi.lavalarm.core.recovery

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface ForceStopRecoveryState : Parcelable {
    data object Unchecked : ForceStopRecoveryState
    data object ShouldPerformRecovery : ForceStopRecoveryState
    data object ShouldNotPerformRecovery : ForceStopRecoveryState
    data object RecoveryPerformed : ForceStopRecoveryState
}
