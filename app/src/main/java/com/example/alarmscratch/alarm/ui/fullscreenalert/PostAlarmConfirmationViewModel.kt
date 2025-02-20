package com.example.alarmscratch.alarm.ui.fullscreenalert

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

class PostAlarmConfirmationViewModel : ViewModel() {

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
                PostAlarmConfirmationViewModel() as T
        }
    }

    fun finishAlarmExecutionFlow(context: Context) {
        context.sendBroadcast(
            Intent().apply {
                action = FullScreenAlarmActivity.ACTION_FINISH_FULL_SCREEN_ALARM_ACTIVITY_NO_CONFIRM
                // On devices running API 34+, it is required to call setPackage() on implicit Intents
                // that are not exported, and are to be used by an application's internal components.
                setPackage(context.packageName)
            }
        )
    }
}
