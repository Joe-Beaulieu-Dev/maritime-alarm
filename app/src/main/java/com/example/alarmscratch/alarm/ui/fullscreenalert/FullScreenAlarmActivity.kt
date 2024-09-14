package com.example.alarmscratch.alarm.ui.fullscreenalert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.alarmexecution.AlarmReceiver
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import java.time.LocalDateTime

class FullScreenAlarmActivity : ComponentActivity() {

    private val fullScreenAlarmReceiver: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (context != null && intent?.action == ACTION_FINISH_FULL_SCREEN_ALARM_ACTIVITY) {
                    finish()
                }
            }
        }

    companion object {
        const val ACTION_FINISH_FULL_SCREEN_ALARM_ACTIVITY = "action_finish_full_screen_alarm_activity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Alarm data
        val alarmId = intent.getIntExtra(AlarmReceiver.EXTRA_ALARM_ID, AlarmReceiver.ALARM_NO_ID)
        val alarmName = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_NAME) ?: applicationContext.getString(R.string.default_alarm_name)
        val alarmDateTime = try {
            val dateTimeString = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_DATE_TIME)
            LocalDateTime.parse(dateTimeString)
        } catch (e: Exception) {
            null
        }

        // Create/Get ViewModel
        val fullScreenAlarmViewModel by viewModels<FullScreenAlarmViewModel> {
            FullScreenAlarmViewModel.provideFactory(alarmId, alarmName, alarmDateTime)
        }

        setContent {
            AlarmScratchTheme {
                FullScreenAlarmScreen(fullScreenAlarmViewModel = fullScreenAlarmViewModel)
            }
        }

        turnScreenOn()
    }

    override fun onResume() {
        super.onResume()

        // Register BroadcastReceiver
        val intentFilter = IntentFilter(ACTION_FINISH_FULL_SCREEN_ALARM_ACTIVITY)
        ContextCompat.registerReceiver(this, fullScreenAlarmReceiver, intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()

        // Unregister BroadcastReceiver
        unregisterReceiver(fullScreenAlarmReceiver)
    }

    private fun turnScreenOn() {
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }
}
