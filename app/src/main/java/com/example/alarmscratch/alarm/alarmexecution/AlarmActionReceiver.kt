package com.example.alarmscratch.alarm.alarmexecution

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlarmActionReceiver : BroadcastReceiver() {

    companion object {
        // Actions
        const val ACTION_START_ALARM = "action_start_alarm"
        const val ACTION_DISMISS_ALARM = "action_dismiss_alarm"

        // Extras
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_ALARM_NAME = "extra_alarm_name"
        const val EXTRA_ALARM_DATE_TIME = "extra_alarm_date_time"
        const val EXTRA_RINGTONE_URI = "extra_ringtone_uri"

        // Other
        const val ALARM_NO_ID = -1
        const val ALARM_NO_RINGTONE_URI = ""
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            when (intent.action) {
                ACTION_START_ALARM ->
                    startAlarm(context, intent)
                ACTION_DISMISS_ALARM ->
                    dismissAlarm(context, intent)
            }
        }
    }

    private fun startAlarm(context: Context, intent: Intent) {
        // TODO: Grab a Wake Lock here. According to the AlarmManager docs, AlarmManger automatically grabs a CPU Wake Lock
        //  and holds it until BroadcastReceiver.onReceive() completes. It also explicitly states that because of this, the
        //  phone may sleep *immediately* after onReceive() completes, so if you call Context.startService() from inside
        //  onReceive() then the phone may sleep before the Service is started.

        val alarmId = intent.getIntExtra(EXTRA_ALARM_ID, ALARM_NO_ID)
        val alarmName = intent.getStringExtra(EXTRA_ALARM_NAME) ?: context.getString(R.string.default_alarm_name)
        val alarmDateTime = intent.getStringExtra(EXTRA_ALARM_DATE_TIME) ?: context.getString(R.string.default_alarm_time)
        val ringtoneUri = intent.getStringExtra(EXTRA_RINGTONE_URI) ?: ALARM_NO_RINGTONE_URI

        // Display Alarm Notification
        val displayNotificationIntent = Intent(context.applicationContext, AlarmNotificationService::class.java).apply {
            // Action
            action = AlarmNotificationService.DISPLAY_ALARM_NOTIFICATION
            // Extras
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_ALARM_NAME, alarmName)
            putExtra(EXTRA_ALARM_DATE_TIME, alarmDateTime)
            putExtra(EXTRA_RINGTONE_URI, ringtoneUri)
        }
        context.applicationContext.startService(displayNotificationIntent)
    }

    private fun dismissAlarm(context: Context, intent: Intent) {
        // Dismiss Alarm Notification
        val dismissNotificationIntent = Intent(context.applicationContext, AlarmNotificationService::class.java).apply {
            action = AlarmNotificationService.DISMISS_ALARM_NOTIFICATION
        }
        context.applicationContext.startService(dismissNotificationIntent)

        // Disable Alarm in database
        val alarmId = intent.getIntExtra(EXTRA_ALARM_ID, ALARM_NO_ID)
        val alarmRepo = AlarmRepository(
            AlarmDatabase
                .getDatabase(context.createDeviceProtectedStorageContext())
                .alarmDao()
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val alarm = async { alarmRepo.getAlarmFlow(alarmId) }.await().first()
                alarmRepo.updateAlarm(alarm.copy(enabled = false))
            } catch (e: Exception) {
                // Flow was empty. Not doing anything with this. Just don't crash.
            }
        }
    }
}
