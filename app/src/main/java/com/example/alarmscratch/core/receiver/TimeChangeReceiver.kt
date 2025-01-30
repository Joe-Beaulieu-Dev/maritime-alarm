package com.example.alarmscratch.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.alarm.alarmexecution.AlarmScheduler
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.core.extension.alarmApplication
import com.example.alarmscratch.core.extension.doAsync
import kotlinx.coroutines.Dispatchers

class TimeChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            when (intent.action) {
                Intent.ACTION_TIME_CHANGED ->
                    onTimeChanged(context)
            }
        }
    }

    private fun onTimeChanged(context: Context) {
        doAsync(context.alarmApplication.applicationScope, Dispatchers.Main) {
            val alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(context.applicationContext).alarmDao())
            AlarmScheduler.refreshAlarms(context, alarmRepository)
        }
    }
}
