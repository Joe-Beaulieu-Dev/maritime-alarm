package com.example.alarmscratch.alarm.alarmexecution

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.zonedEpochMillis
import java.time.LocalDateTime

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun scheduleInitialAlarm(alarm: Alarm) {
        scheduleAlarm(
            alarm.id,
            alarm.name,
            alarm.dateTime,
            alarm.ringtoneUriString,
            alarm.isVibrationEnabled,
            alarm.snoozeDuration
        )
    }

    override fun scheduleSnoozedAlarm(alarm: Alarm) {
        scheduleAlarm(
            alarm.id,
            alarm.name,
            alarm.snoozeDateTime ?: LocalDateTimeUtil.defaultSnoozeDateTime(),
            alarm.ringtoneUriString,
            alarm.isVibrationEnabled,
            alarm.snoozeDuration
        )
    }

    private fun scheduleAlarm(
        alarmId: Int,
        alarmName: String,
        alarmExecutionDateTime: LocalDateTime,
        ringtoneUri: String,
        isVibrationEnabled: Boolean,
        snoozeDuration: Int
    ) {
        // Create PendingIntent to execute Alarm
        val alarmIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            // Action
            action = AlarmActionReceiver.ACTION_EXECUTE_ALARM
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_NAME, alarmName)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_EXECUTION_DATE_TIME, alarmExecutionDateTime.toString())
            putExtra(AlarmActionReceiver.EXTRA_RINGTONE_URI, ringtoneUri)
            putExtra(AlarmActionReceiver.EXTRA_IS_VIBRATION_ENABLED, isVibrationEnabled)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_SNOOZE_DURATION, snoozeDuration)
        }
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule Alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmExecutionDateTime.zonedEpochMillis(),
            alarmPendingIntent
        )
    }

    override fun cancelAlarm(alarm: Alarm) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarm.id,
                Intent(context, AlarmActionReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}
