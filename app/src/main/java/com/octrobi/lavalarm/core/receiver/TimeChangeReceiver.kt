package com.octrobi.lavalarm.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.octrobi.lavalarm.alarm.alarmexecution.AlarmScheduler
import com.octrobi.lavalarm.alarm.data.repository.AlarmDatabase
import com.octrobi.lavalarm.alarm.data.repository.AlarmRepository
import com.octrobi.lavalarm.core.extension.alarmApplication
import com.octrobi.lavalarm.core.extension.doAsync
import kotlinx.coroutines.Dispatchers

class TimeChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            when (intent.action) {
                Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_DATE_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED ->
                    onTimeChanged(context)
            }
        }
    }

    private fun onTimeChanged(context: Context) {
        doAsync(context.alarmApplication.applicationScope, Dispatchers.Main) {
            val alarmRepository = AlarmRepository(
                AlarmDatabase
                    .getDatabase(context.createDeviceProtectedStorageContext())
                    .alarmDao()
            )
            AlarmScheduler.refreshAlarms(context, alarmRepository)
        }
    }
}
