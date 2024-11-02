package com.example.alarmscratch.alarm.alarmexecution

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData
import com.example.alarmscratch.core.extension.zonedEpochMillis

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun scheduleInitialAlarm(alarmExecutionData: AlarmExecutionData) {
        scheduleAlarm(alarmExecutionData)
    }

    override fun scheduleSnoozedAlarm(alarmExecutionData: AlarmExecutionData) {
        scheduleAlarm(alarmExecutionData)
    }

    private fun scheduleAlarm(alarmExecutionData: AlarmExecutionData) {
        // Create PendingIntent to execute Alarm
        val alarmIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            // Action
            action = AlarmActionReceiver.ACTION_EXECUTE_ALARM
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmExecutionData.id)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_NAME, alarmExecutionData.name)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_EXECUTION_DATE_TIME, alarmExecutionData.executionDateTime.toString())
            putExtra(AlarmActionReceiver.EXTRA_RINGTONE_URI, alarmExecutionData.ringtoneUri)
            putExtra(AlarmActionReceiver.EXTRA_IS_VIBRATION_ENABLED, alarmExecutionData.isVibrationEnabled)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_SNOOZE_DURATION, alarmExecutionData.snoozeDuration)
        }
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmExecutionData.id,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule Alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmExecutionData.executionDateTime.zonedEpochMillis(),
            alarmPendingIntent
        )
    }

    override fun cancelAlarm(alarmExecutionData: AlarmExecutionData) {
        // Create PendingIntent to cancel Alarm
        val alarmIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            // This needs to be the same as the scheduling action.
            // This is because the Intent must pass Intent.filterEquals(), which looks at the flag,
            // in order for the AlarmManager to be able to find the Alarm with the "same Intent" to cancel.
            action = AlarmActionReceiver.ACTION_EXECUTE_ALARM
        }

        // Cancel Alarm
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarmExecutionData.id,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}
