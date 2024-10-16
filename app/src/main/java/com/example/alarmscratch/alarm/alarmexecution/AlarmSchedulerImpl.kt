package com.example.alarmscratch.alarm.alarmexecution

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.alarm.data.model.Alarm
import java.time.ZoneId

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun scheduleAlarm(alarm: Alarm) {
        // Create PendingIntent to start Alarm
        val startAlarmIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            // Action
            action = AlarmActionReceiver.ACTION_START_ALARM
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarm.id)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_NAME, alarm.name)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_DATE_TIME, alarm.dateTime.toString())
            putExtra(AlarmActionReceiver.EXTRA_RINGTONE_URI, alarm.ringtoneUriString)
            putExtra(AlarmActionReceiver.EXTRA_IS_VIBRATION_ENABLED, alarm.isVibrationEnabled)
        }
        val startAlarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            startAlarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarm.dateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            startAlarmPendingIntent
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
