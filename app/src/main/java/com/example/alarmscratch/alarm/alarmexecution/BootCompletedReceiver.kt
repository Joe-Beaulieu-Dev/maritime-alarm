package com.example.alarmscratch.alarm.alarmexecution

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.isSnoozed
import com.example.alarmscratch.core.extension.toAlarmExecutionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent?.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            val alarmRepo = AlarmRepository(
                AlarmDatabase
                    .getDatabase(context.createDeviceProtectedStorageContext())
                    .alarmDao()
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val alarmList = async { alarmRepo.getAllAlarmsFlow() }.await().first()
                    rescheduleEligibleAlarms(context, alarmList)
                } catch (e: Exception) {
                    // Flow was empty. Not doing anything with this. Just don't crash.
                }
            }
        }
    }

    /**
     * Reschedule Alarms if they meet the following criteria:
     *
     * 1) Are enabled
     * 2) Are scheduled to execute either now, or in the future
     *
     * @param context Context to be used for scheduling Alarms
     * @param alarmList List of Alarms to potentially reschedule
     */
    private fun rescheduleEligibleAlarms(context: Context, alarmList: List<Alarm>) {
        alarmList
            .filter { alarm ->
                alarm.enabled &&
                        if (!alarm.isSnoozed()) {
                            !alarm.dateTime.isBefore(LocalDateTimeUtil.nowTruncated())
                        } else {
                            // TODO: Think of a better default behavior for this
                            alarm.snoozeDateTime?.isBefore(LocalDateTimeUtil.nowTruncated())?.not() ?: false
                        }
            }
            .forEach { AlarmScheduler.scheduleAlarm(context, it.toAlarmExecutionData()) }
    }
}
