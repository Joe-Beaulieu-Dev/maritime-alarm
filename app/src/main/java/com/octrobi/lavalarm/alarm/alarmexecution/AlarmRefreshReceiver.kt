package com.octrobi.lavalarm.alarm.alarmexecution

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.octrobi.lavalarm.alarm.data.repository.AlarmDatabase
import com.octrobi.lavalarm.alarm.data.repository.AlarmRepository
import com.octrobi.lavalarm.core.extension.alarmApplication
import com.octrobi.lavalarm.core.extension.doAsync
import kotlinx.coroutines.Dispatchers

/**
 * Clean and reschedule Alarms in various scenarios in reaction to system Intent actions.
 *
 * Scenarios include, but are not limited to: device boot, device date/time change, etc.
 */
class AlarmRefreshReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            when (intent.action) {
                Intent.ACTION_LOCKED_BOOT_COMPLETED,
                Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_DATE_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED,
                Intent.ACTION_MY_PACKAGE_REPLACED,
                // Contrary to what the name may suggest, this action is only sent
                // by the system when the permission changes from denied to granted.
                AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED ->
                    refreshAlarms(context)
            }
        }
    }

    /**
     * Clean and reschedule Alarms as long as one of the following two conditions are met:
     *   1) The Device is running on API >= 33
     *   2) The Device is running on API < 33, and has the SCHEDULE_EXACT_ALARM permission granted
     *
     * If neither of the above two conditions are met, then this method is a no-op.
     *
     * @param context Context used to perform various functions related to the System
     */
    private fun refreshAlarms(context: Context) {
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
