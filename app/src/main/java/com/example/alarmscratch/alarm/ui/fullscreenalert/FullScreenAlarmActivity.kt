package com.example.alarmscratch.alarm.ui.fullscreenalert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.alarmexecution.AlarmActionReceiver
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.getSerializableExtraSafe
import com.example.alarmscratch.core.navigation.Destination
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.AndroidDefaultDarkScrim
import com.example.alarmscratch.settings.data.repository.AlarmDefaultsRepository
import java.time.LocalDateTime

class FullScreenAlarmActivity : ComponentActivity() {

    // Navigation State
    private lateinit var navHostController: NavHostController

    // BroadcastReceiver
    private var receiverRegistered = false
    private val fullScreenAlarmReceiver: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (context != null && intent != null) {
                    when (intent.action) {
                        ACTION_SHOW_POST_ALARM_CONFIRMATION ->
                            navigateToConfirmationScreen(intent)
                        ACTION_FINISH_FULL_SCREEN_ALARM_FLOW ->
                            finishActivity()
                    }
                }
            }

            private fun navigateToConfirmationScreen(intent: Intent) {
                // TODO: Come up with a better default than just FullScreenAlarmButton.BOTH
                // Post Alarm confirmation data
                val fullScreenAlarmButton = intent.getSerializableExtraSafe(
                    AlarmActionReceiver.EXTRA_FULL_SCREEN_ALARM_BUTTON,
                    FullScreenAlarmButton::class.java
                ) ?: FullScreenAlarmButton.BOTH
                val snoozeDuration = intent.getIntExtra(
                    AlarmActionReceiver.EXTRA_ALARM_SNOOZE_DURATION,
                    AlarmDefaultsRepository.DEFAULT_SNOOZE_DURATION
                )

                // Navigate to PostAlarmConfirmationScreen
                // Do so in such a way that the User cannot navigate back to the FullScreenAlarmScreen
                // Navigating back will simply exit the full screen Alarm flow
                navHostController.navigate(Destination.PostAlarmConfirmationScreen(fullScreenAlarmButton, snoozeDuration)) {
                    popUpTo(navHostController.graph.findStartDestination().id) { inclusive = true }
                }
            }

            private fun finishActivity() {
                finish()
            }
        }

    companion object {
        // BroadcastReceiver constants
        const val ACTION_SHOW_POST_ALARM_CONFIRMATION = "action_show_post_alarm_confirmation"
        const val ACTION_FINISH_FULL_SCREEN_ALARM_FLOW = "action_finish_full_screen_alarm_flow"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge to edge for dynamic Status Bar coloring
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidDefaultDarkScrim.toArgb())
        )

        // Alarm data
        val dateTime = try {
            LocalDateTime.parse(intent.getStringExtra(AlarmActionReceiver.EXTRA_ALARM_EXECUTION_DATE_TIME))
        } catch (e: Exception) {
            // The execution DateTime for the Alarm should be for right now, so this fallback makes sense.
            LocalDateTimeUtil.nowTruncated()
        }
        val is24Hour = intent.getBooleanExtra(AlarmActionReceiver.EXTRA_IS_24_HOUR, AlarmActionReceiver.ALARM_NO_IS_24_HOUR)
        val alarmExecutionData = AlarmExecutionData(
            id = intent.getIntExtra(AlarmActionReceiver.EXTRA_ALARM_ID, AlarmActionReceiver.ALARM_NO_ID),
            name = intent.getStringExtra(AlarmActionReceiver.EXTRA_ALARM_NAME) ?: getString(R.string.default_alarm_name),
            executionDateTime = dateTime,
            encodedRepeatingDays = intent.getIntExtra(
                AlarmActionReceiver.EXTRA_REPEATING_DAYS,
                AlarmActionReceiver.ALARM_MISSING_REPEATING_DAYS
            ),
            ringtoneUri = intent.getStringExtra(AlarmActionReceiver.EXTRA_RINGTONE_URI) ?: AlarmActionReceiver.ALARM_NO_RINGTONE_URI,
            isVibrationEnabled = intent.getBooleanExtra(
                AlarmActionReceiver.EXTRA_IS_VIBRATION_ENABLED,
                AlarmActionReceiver.ALARM_NO_IS_VIBRATION_ENABLED
            ),
            snoozeDuration = intent.getIntExtra(
                AlarmActionReceiver.EXTRA_ALARM_SNOOZE_DURATION,
                AlarmDefaultsRepository.DEFAULT_SNOOZE_DURATION
            )
        )

        // Create/Get ViewModel
        val fullScreenAlarmViewModel by viewModels<FullScreenAlarmViewModel> {
            FullScreenAlarmViewModel.provideFactory(alarmExecutionData, is24Hour)
        }

        setContent {
            AlarmScratchTheme {
                navHostController = rememberNavController()
                FullScreenAlarmNavHost(
                    navHostController = navHostController,
                    fullScreenAlarmViewModel = fullScreenAlarmViewModel
                )
            }
        }

        turnScreenOn()
    }

    override fun onStart() {
        super.onStart()

        // If onStart() is called while we're currently on PostAlarmConfirmationScreen then
        // that means the User has already Snoozed or Dismissed the Alarm, and they have already
        // visited this screen before. This means that the PostAlarmConfirmationScreen was previously
        // displayed, then navigated away from (screen turned off), then displayed a second time.
        // PostAlarmConfirmationScreen should not be displayed a second time if the User turned the
        // screen off the first time around, so just finish the Activity.
        val onPostAlarmConfirmationScreen: Boolean? = if (::navHostController.isInitialized) {
            navHostController.currentDestination?.hasRoute<Destination.PostAlarmConfirmationScreen>()
        } else {
            null
        }
        if (onPostAlarmConfirmationScreen == true) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        // Register BroadcastReceiver
        if (!receiverRegistered) {
            val intentFilter = IntentFilter().apply {
                addAction(ACTION_SHOW_POST_ALARM_CONFIRMATION)
                addAction(ACTION_FINISH_FULL_SCREEN_ALARM_FLOW)
            }
            ContextCompat.registerReceiver(this, fullScreenAlarmReceiver, intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)
            receiverRegistered = true
        }
    }

    override fun onPause() {
        super.onPause()

        // Unregister BroadcastReceiver.
        // Don't unregister if it hasn't been registered for some reason
        // because this will lead to an IllegalArgumentException.
        if (receiverRegistered) {
            unregisterReceiver(fullScreenAlarmReceiver)
            receiverRegistered = false
        }
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
