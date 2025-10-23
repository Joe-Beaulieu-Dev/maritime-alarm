package com.octrobi.lavalarm.alarm.ui.fullscreenalert

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.octrobi.lavalarm.core.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostAlarmConfirmationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Alarm and Display data
    private val navigationRoute = savedStateHandle.toRoute<Destination.PostAlarmConfirmationScreen>()
    val fullScreenAlarmButton = navigationRoute.fullScreenAlarmButton
    val snoozeDuration = navigationRoute.snoozeDuration

    /*
     * Action
     */

    fun finishFullScreenAlarmFlow(context: Context) {
        context.sendBroadcast(
            Intent().apply {
                action = FullScreenAlarmActivity.ACTION_FINISH_FULL_SCREEN_ALARM_FLOW
                // On devices running API 34+, it is required to call setPackage() on implicit Intents
                // that are not exported, and are to be used by an application's internal components.
                setPackage(context.packageName)
            }
        )
    }
}
