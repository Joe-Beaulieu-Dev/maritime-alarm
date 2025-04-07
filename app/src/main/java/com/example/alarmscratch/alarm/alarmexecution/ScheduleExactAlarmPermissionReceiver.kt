package com.example.alarmscratch.alarm.alarmexecution

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.core.extension.alarmApplication
import com.example.alarmscratch.core.extension.doAsync
import kotlinx.coroutines.Dispatchers

/**
 * Reschedule Alarms, if possible, when the SCHEDULE_EXACT_ALARM permission changes from Denied to Granted.
 * This BroadcastReceiver is only relevant on, and will only be called on APIs < 33. This is because:
 *   1) Devices running APIs < 33 will request SCHEDULE_EXACT_ALARM, while Devices running APIs >= 33 will be
 *   auto-granted USE_EXACT_ALARM instead. USE_EXACT_ALARM, which didn't exist before API 33, cannot be revoked
 *   and replaces the need for SCHEDULE_EXACT_ALARM.
 *   2) This BroadcastReceiver only listens for AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED
 *   which is only relevant on devices requiring SCHEDULE_EXACT_ALARM for scheduling exact Alarms
 *
 * Because of this, it is not necessary to check the API level in any of the functions in this class since we know
 * that this BroadcastReceiver will never be invoked on APIs >= 33.
 */
class ScheduleExactAlarmPermissionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            when(intent.action) {
                // In spite of what the name may suggest, this action is only sent
                // by the system when the permission changes from denied to granted
                AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED ->
                    cleanAndRescheduleAlarms(context)
            }
        }
    }

    /**
     * Clean and reschedule Alarms as long as the Device has the SCHEDULE_EXACT_ALARM permission granted
     *
     * @param context Context used to perform various functions related to the System
     */
    private fun cleanAndRescheduleAlarms(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (alarmManager.canScheduleExactAlarms()) {
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
