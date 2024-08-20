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
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_ALARM_NAME = "extra_alarm_name"
        const val EXTRA_ALARM_TIME = "extra_alarm_time"
        const val EXTRA_RINGTONE_URI_STRING = "extra_ringtone_uri_string"
        const val ALARM_NO_ID = -1
        const val ALARM_NO_RINGTONE_URI = ""
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val service = AlarmNotificationService(context)
            val alarmId = intent.getIntExtra(EXTRA_ALARM_ID, ALARM_NO_ID)
            val alarmName = intent.getStringExtra(EXTRA_ALARM_NAME) ?: context.getString(R.string.default_alarm_name)
            val alarmTime = intent.getStringExtra(EXTRA_ALARM_TIME) ?: context.getString(R.string.default_alarm_time)
            val ringtoneUriString = intent.getStringExtra(EXTRA_RINGTONE_URI_STRING) ?: ALARM_NO_RINGTONE_URI

            // If there's no Alarm ID then something's wrong. Do not handle event.
            if (alarmId == ALARM_NO_ID) return

            // TODO: Check for lock status here. If screen is on, but it's locked, we want to show the full screen notification.
            if (isDisplayOn(context)) {
                service.showNotification(alarmId, alarmName, alarmTime, ringtoneUriString)
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
