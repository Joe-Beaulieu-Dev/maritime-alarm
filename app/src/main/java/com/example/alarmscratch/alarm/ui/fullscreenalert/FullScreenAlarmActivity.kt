package com.example.alarmscratch.alarm.ui.fullscreenalert

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.alarmexecution.AlarmReceiver
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import java.time.LocalDateTime

class FullScreenAlarmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val alarmName = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_NAME) ?: applicationContext.getString(R.string.default_alarm_name)
        val alarmDateTime = try {
            val dateTimeString = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_DATE_TIME)
            LocalDateTime.parse(dateTimeString)
        } catch (e: Exception) {
            null
        }

        setContent {
            AlarmScratchTheme {
                FullScreenAlarmScreen(
                    alarmName = alarmName,
                    alarmDateTime = alarmDateTime
                )
            }
        }

        turnScreenOn()
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
