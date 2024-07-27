package com.example.alarmscratch.alarm.alarmexecution

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent?.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            val alarmScheduler = AlarmSchedulerImpl(context)
            val alarmRepo = AlarmRepository(
                AlarmDatabase
                    .getDatabase(context.createDeviceProtectedStorageContext())
                    .alarmDao()
            )

            CoroutineScope(Dispatchers.IO).launch {
                alarmRepo.getAllAlarmsFlow().collect { alarmList ->
                    alarmList
                        .filter { it.enabled && !it.dateTime.isBefore(LocalDateTimeUtil.nowTruncated()) }
                        .forEach { alarm ->
                            alarmScheduler.scheduleAlarm(alarm)
                        }
                }
            }
        }
    }
}
