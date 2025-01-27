package com.example.alarmscratch.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimeChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            when (intent.action) {
                Intent.ACTION_TIME_CHANGED ->
                    onTimeChanged()
            }
        }
    }

    private fun onTimeChanged() {}
}
