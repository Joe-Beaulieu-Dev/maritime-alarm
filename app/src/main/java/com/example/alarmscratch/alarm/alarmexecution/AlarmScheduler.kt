package com.example.alarmscratch.alarm.alarmexecution

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.alarm.util.AlarmUtil
import com.example.alarmscratch.core.extension.toAlarmExecutionData
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

        // Create AlarmClockInfo
        val alarmClockInfo = AlarmClockInfo(
            alarmExecutionData.executionDateTime.zonedEpochMillis(),
            alarmPendingIntent
        )

        // Schedule Alarm
        getAlarmManager(context).setAlarmClock(
            alarmClockInfo,
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

    suspend fun refreshAlarms(context: Context, alarmRepository: AlarmRepository) {
        // Get all enabled Alarms
        val enabledAlarms = alarmRepository.getAllEnabledAlarms()

        // Clean Alarms
        enabledAlarms.forEach { AlarmUtil.cleanAlarm(it, alarmRepository) }

        // Get all enabled Alarms post cleaning, as AlarmExecutionData
        val cleanAlarmExecutionData = alarmRepository.getAllEnabledAlarms().map { it.toAlarmExecutionData() }

        // Reschedule Alarms
        val alarmManager = getAlarmManager(context)
        cleanAlarmExecutionData.forEach { alarm ->
            val alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id,
                AlarmIntentBuilder.executeAlarmIntent(context, alarm),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setAlarmClock(
                AlarmClockInfo(
                    alarm.executionDateTime.zonedEpochMillis(),
                    alarmPendingIntent
                ),
                alarmPendingIntent
            )
        }
    }

    private fun getAlarmManager(context: Context): AlarmManager =
        context.getSystemService(AlarmManager::class.java)
}
