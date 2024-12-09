package com.example.alarmscratch.alarm.alarmexecution

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.alarm.util.AlarmUtil
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.alarmApplication
import com.example.alarmscratch.core.extension.doAsync
import com.example.alarmscratch.core.extension.isDirty
import com.example.alarmscratch.core.extension.isRepeating
import com.example.alarmscratch.core.extension.isSnoozed
import com.example.alarmscratch.core.extension.toAlarmExecutionData
import kotlinx.coroutines.Dispatchers

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            when (intent.action) {
                Intent.ACTION_LOCKED_BOOT_COMPLETED ->
                    cleanAndRescheduleAlarms(context)
            }
        }
    }

    private fun cleanAndRescheduleAlarms(context: Context) {
        val alarmRepository = AlarmRepository(
            AlarmDatabase
                .getDatabase(context.createDeviceProtectedStorageContext())
                .alarmDao()
        )

        doAsync(context.alarmApplication.applicationScope, Dispatchers.IO) {
            // Clean Alarms
            cleanAllAlarms(alarmRepository)

            // Reschedule Alarms
            // Re-query the database to get up to date Alarm data after cleaning the Alarms
            rescheduleEligibleAlarms(context, alarmRepository.getAllAlarms())
        }
    }

    private suspend fun cleanAllAlarms(alarmRepository: AlarmRepository) {
        alarmRepository.getAllAlarms().forEach { cleanAlarm(it, alarmRepository) }
    }

    /**
     * Cleans dirty Alarms as determined by Alarm.isDirty().
     * Alarms that are already clean are a no-op.
     *
     * To clean an Alarm is to:
     * - Reset snooze
     * - If Alarm is repeating -> set to next repeating date/time
     * - If Alarm is not repeating -> disable Alarm
     *
     * @param alarm Alarm candidate for cleaning
     * @param alarmRepository repository in which cleaned Alarms are updated
     */
    private suspend fun cleanAlarm(alarm: Alarm, alarmRepository: AlarmRepository) {
        if (alarm.isDirty()) {
            // Clean Alarm
            val cleanAlarm = alarm.run {
                if (isRepeating()) {
                    copy(
                        dateTime = AlarmUtil.nextRepeatingDateTime(alarm.dateTime, alarm.weeklyRepeater),
                        snoozeDateTime = null
                    )
                } else {
                    copy(enabled = false, snoozeDateTime = null)
                }
            }

            // Update database
            alarmRepository.updateAlarm(cleanAlarm)
        }
    }

    /**
     * Reschedule Alarms if they meet the following criteria:
     * 1) Are enabled
     * 2) Are configured to execute in the future
     *
     * @param context Context to be used for scheduling Alarms
     * @param alarmList List of Alarms to potentially reschedule
     */
    private fun rescheduleEligibleAlarms(context: Context, alarmList: List<Alarm>) {
        val now = LocalDateTimeUtil.nowTruncated()
        alarmList
            .filter { alarm ->
                alarm.enabled &&
                        if (alarm.isSnoozed()) {
                            alarm.snoozeDateTime?.isAfter(now) == true
                        } else {
                            alarm.dateTime.isAfter(now)
                        }
            }
            .forEach { AlarmScheduler.scheduleAlarm(context, it.toAlarmExecutionData()) }
    }
}
