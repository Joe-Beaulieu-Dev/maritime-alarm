package com.example.alarmscratch.alarm.alarmexecution

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.ui.notification.AlarmNotificationService

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_ALARM_NAME = "extra_alarm_name"
        const val EXTRA_ALARM_DATE_TIME = "extra_alarm_date_time"
        const val EXTRA_RINGTONE_URI = "extra_ringtone_uri"
        const val ALARM_NO_ID = -1
        const val ALARM_NO_RINGTONE_URI = ""
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val service = AlarmNotificationService(context)
            val alarmId = intent.getIntExtra(EXTRA_ALARM_ID, ALARM_NO_ID)
            val alarmName = intent.getStringExtra(EXTRA_ALARM_NAME) ?: context.getString(R.string.default_alarm_name)
            val alarmDateTime = intent.getStringExtra(EXTRA_ALARM_DATE_TIME) ?: context.getString(R.string.default_alarm_time)
            val ringtoneUri = intent.getStringExtra(EXTRA_RINGTONE_URI) ?: ALARM_NO_RINGTONE_URI

            // If there's no Alarm ID then something's wrong. Do not handle event.
            if (alarmId == ALARM_NO_ID) return

            service.showFullScreenNotification(alarmId, alarmName, alarmDateTime, ringtoneUri)
        }
    }
}
