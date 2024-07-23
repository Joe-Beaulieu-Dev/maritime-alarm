package com.example.alarmscratch.alarm.alarmexecution

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.ui.notification.AlarmNotificationService

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ALARM_NAME = "EXTRA_ALARM_NAME"
        const val EXTRA_ALARM_TIME = "EXTRA_ALARM_TIME"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val alarmName = intent.getStringExtra(EXTRA_ALARM_NAME) ?: context.getString(R.string.default_alarm_name)
            val alarmTime = intent.getStringExtra(EXTRA_ALARM_TIME) ?: ""
            val service = AlarmNotificationService(context)
            service.showNotification(alarmName, alarmTime)
        }
    }
}
