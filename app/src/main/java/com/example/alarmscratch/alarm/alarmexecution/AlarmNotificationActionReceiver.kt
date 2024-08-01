package com.example.alarmscratch.alarm.alarmexecution

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmNotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DISMISS_ALARM = "action_dismiss_alarm"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val alarmId = intent.getIntExtra(AlarmReceiver.EXTRA_ALARM_ID, AlarmReceiver.ALARM_NO_ID)

            // If there's no Alarm ID then something's wrong. Do not handle event.
            if (alarmId == AlarmReceiver.ALARM_NO_ID) return

            if (intent.action == ACTION_DISMISS_ALARM) {
                dismissAlarm(context, alarmId)
            }
        }
    }

    private fun dismissAlarm(context: Context, alarmId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(alarmId)
    }
}
