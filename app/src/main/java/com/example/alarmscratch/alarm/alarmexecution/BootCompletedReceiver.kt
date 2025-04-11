package com.example.alarmscratch.alarm.alarmexecution

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.core.extension.alarmApplication
import com.example.alarmscratch.core.extension.doAsync
import kotlinx.coroutines.Dispatchers

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            when (intent.action) {
                Intent.ACTION_LOCKED_BOOT_COMPLETED ->
                    cleanAndRescheduleAlarmsIfPossible(context)
            }
        }
    }

    /**
     * Clean and reschedule Alarms as long as one of the two following conditions are met:
     *   1) The Device is running an API >= 33
     *   2) The Device is running an API < 33, and has the SCHEDULE_EXACT_ALARM permission granted
     *
     * If neither of the above two conditions are met, then this method is a no-op.
     *
     * @param context Context used to perform various functions related to the System
     */
    private fun cleanAndRescheduleAlarmsIfPossible(context: Context) {
        val canRescheduleAlarms =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                val alarmManager = context.getSystemService(AlarmManager::class.java)
                alarmManager.canScheduleExactAlarms()
            } else {
                // This app declares the USE_EXACT_ALARM permission on APIs 33+, which is auto-granted
                // and cannot be revoked. Therefore, we can always schedule Alarms on APIs 33+.
                true
            }

        if (canRescheduleAlarms) {
            val alarmRepository = AlarmRepository(
                AlarmDatabase
                    .getDatabase(context.createDeviceProtectedStorageContext())
                    .alarmDao()
            )

            doAsync(context.alarmApplication.applicationScope, Dispatchers.IO) {
                AlarmScheduler.cleanAndRescheduleAlarms(context, alarmRepository)
            }
        }
    }
}
