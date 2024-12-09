package com.example.alarmscratch.alarm.alarmexecution

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData
import com.example.alarmscratch.alarm.ui.fullscreenalert.FullScreenAlarmActivity
import com.example.alarmscratch.alarm.ui.notification.AlarmNotification
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.ringtone.RingtonePlayerManager
import com.example.alarmscratch.settings.data.model.GeneralSettings
import com.example.alarmscratch.settings.data.repository.AlarmDefaultsRepository
import com.example.alarmscratch.settings.data.repository.GeneralSettingsRepository
import com.example.alarmscratch.settings.data.repository.generalSettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class AlarmNotificationService : Service() {

    // Coroutine
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(job)

    companion object {
        // Actions
        const val DISPLAY_ALARM_NOTIFICATION = "display_alarm_notification"
        const val DISMISS_ALARM_NOTIFICATION = "dismiss_alarm_notification"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            DISPLAY_ALARM_NOTIFICATION ->
                displayAlarmNotification(intent)
            DISMISS_ALARM_NOTIFICATION ->
                dismissAlarmNotification()
        }

        return START_NOT_STICKY
    }

    private fun displayAlarmNotification(intent: Intent) {
        // Alarm data
        val id = intent.getIntExtra(AlarmActionReceiver.EXTRA_ALARM_ID, AlarmActionReceiver.ALARM_NO_ID)
        val executionDateTime = try {
            LocalDateTime.parse(intent.getStringExtra(AlarmActionReceiver.EXTRA_ALARM_EXECUTION_DATE_TIME))
        } catch (e: Exception) {
            // This is for use in the Alarm Notification, which is about to be set off below.
            // The execution DateTime for the Alarm should be for right now anyways, so this fallback makes sense.
            LocalDateTimeUtil.nowTruncated()
        }
        val ringtoneUri = intent.getStringExtra(AlarmActionReceiver.EXTRA_RINGTONE_URI) ?: AlarmActionReceiver.ALARM_NO_RINGTONE_URI
        val isVibrationEnabled = intent.getBooleanExtra(
            AlarmActionReceiver.EXTRA_IS_VIBRATION_ENABLED,
            AlarmActionReceiver.ALARM_NO_IS_VIBRATION_ENABLED
        )
        val alarmExecutionData = AlarmExecutionData(
            id = id,
            name = intent.getStringExtra(AlarmActionReceiver.EXTRA_ALARM_NAME) ?: getString(R.string.default_alarm_name),
            executionDateTime = executionDateTime,
            encodedRepeatingDays = intent.getIntExtra(
                AlarmActionReceiver.EXTRA_REPEATING_DAYS,
                AlarmActionReceiver.ALARM_MISSING_REPEATING_DAYS
            ),
            ringtoneUri = ringtoneUri,
            isVibrationEnabled = isVibrationEnabled,
            snoozeDuration = intent.getIntExtra(
                AlarmActionReceiver.EXTRA_ALARM_SNOOZE_DURATION,
                AlarmDefaultsRepository.DEFAULT_SNOOZE_DURATION
            )
        )

        // Get General Settings, Launch Notification, Play Ringtone, Vibrate
        coroutineScope.launch(Dispatchers.Main) {
            // Get General Settings
            val generalSettingsRepository = GeneralSettingsRepository(applicationContext.generalSettingsDataStore)
            val generalSettings = try {
                generalSettingsRepository.generalSettingsFlow.first()
            } catch (e: NoSuchElementException) {
                // Flow was empty. Return GeneralSettings with defaults.
                GeneralSettings(GeneralSettingsRepository.DEFAULT_TIME_DISPLAY)
            }

            // Create Notification
            val fullScreenNotification = AlarmNotification.fullScreenNotification(
                applicationContext,
                alarmExecutionData,
                generalSettings.timeDisplay
            )

            // Push Service to foreground and display Notification
            startForeground(id, fullScreenNotification)

            // TODO: Check notification permission before sounding Alarm. If you don't,
            //  then the ringtone will sound without the notification.
            // Play Ringtone
            RingtonePlayerManager.startAlarmSound(applicationContext, ringtoneUri)

            // Start Vibration
            if (isVibrationEnabled) {
                VibrationController.startVibration(applicationContext)
            }
        }
    }

    private fun dismissAlarmNotification() {
        // Finish FullScreenAlarmActivity
        val dismissFullScreenAlertIntent = Intent().apply {
            action = FullScreenAlarmActivity.ACTION_FINISH_FULL_SCREEN_ALARM_ACTIVITY
            // On devices running API 34+, it is required to call setPackage() on implicit Intents
            // that are not exported, and are to be used by an application's internal components.
            setPackage(this@AlarmNotificationService.packageName)
        }
        applicationContext.sendBroadcast(dismissFullScreenAlertIntent)

        // Stop Ringtone
        RingtonePlayerManager.stopAlarmSound()

        // Stop Vibration
        VibrationController.stopVibration(applicationContext)

        // Release WakeLock
        WakeLockManager.releaseWakeLock()

        // Stop Service, which dismisses the Notification
        stopSelf()
    }

    override fun onDestroy() {
        // Cancel coroutines
        job.cancel()
    }
}
