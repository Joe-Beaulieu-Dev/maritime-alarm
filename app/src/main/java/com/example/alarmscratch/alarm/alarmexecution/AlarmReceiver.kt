package com.example.alarmscratch.alarm.alarmexecution

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.view.Display
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.ui.notification.AlarmNotificationService

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ALARM_NAME = "EXTRA_ALARM_NAME"
        const val EXTRA_ALARM_TIME = "EXTRA_ALARM_TIME"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val service = AlarmNotificationService(context)
            val alarmName = intent.getStringExtra(EXTRA_ALARM_NAME) ?: context.getString(R.string.default_alarm_name)
            val alarmTime = intent.getStringExtra(EXTRA_ALARM_TIME) ?: context.getString(R.string.default_alarm_time)

            // TODO: Check for lock status here. If screen is on, but it's locked, we want to show the full screen notification.
            if (isDisplayOn(context)) {
                service.showNotification(alarmName, alarmTime)
            } else {
                service.showFullScreenNotification(alarmName, alarmTime)
            }
        }
    }

    private fun isDisplayOn(context: Context): Boolean {
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.displays.forEach {
            if (it.state == Display.STATE_ON) {
                return true
            }
        }
        return false
    }
}
