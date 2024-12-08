package com.example.alarmscratch.alarm.alarmexecution

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData
import com.example.alarmscratch.core.extension.zonedEpochMillis

object AlarmScheduler {

    fun scheduleAlarm(context: Context, alarmExecutionData: AlarmExecutionData) {
        // Create PendingIntent to execute Alarm
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmExecutionData.id,
            AlarmIntentBuilder.executeAlarmIntent(context, alarmExecutionData),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule Alarm
        getAlarmManager(context).setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmExecutionData.executionDateTime.zonedEpochMillis(),
            alarmPendingIntent
        )
    }

    fun cancelAlarm(context: Context, alarmExecutionData: AlarmExecutionData) {
        // Create PendingIntent to cancel Alarm.
        // This Intent is never executed. It is only used by the AlarmManager for Alarm cancellation.
        val alarmIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            // This action needs to be the same as the scheduling action.
            // This is because the Intent must pass Intent.filterEquals(), which looks at the action among other things,
            // in order for the AlarmManager to be able to find the Alarm with the "same Intent" to cancel.
            action = AlarmActionReceiver.ACTION_EXECUTE_ALARM
        }

        // Cancel Alarm
        getAlarmManager(context).cancel(
            PendingIntent.getBroadcast(
                context,
                alarmExecutionData.id,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    private fun getAlarmManager(context: Context): AlarmManager =
        context.getSystemService(AlarmManager::class.java)
}
