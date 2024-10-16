package com.example.alarmscratch.alarm.alarmexecution

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.ui.fullscreenalert.FullScreenAlarmActivity
import com.example.alarmscratch.alarm.ui.notification.AlarmNotification
import com.example.alarmscratch.core.ringtone.RingtonePlayerManager
import com.example.alarmscratch.settings.data.model.GeneralSettings
import com.example.alarmscratch.settings.data.repository.GeneralSettingsRepository
import com.example.alarmscratch.settings.data.repository.generalSettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmNotificationService : Service() {

    companion object {
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
        val alarmId = intent.getIntExtra(AlarmActionReceiver.EXTRA_ALARM_ID, AlarmActionReceiver.ALARM_NO_ID)
        val alarmName = intent.getStringExtra(AlarmActionReceiver.EXTRA_ALARM_NAME) ?: getString(R.string.default_alarm_name)
        val alarmDateTime = intent.getStringExtra(AlarmActionReceiver.EXTRA_ALARM_DATE_TIME) ?: getString(R.string.default_alarm_time)
        val ringtoneUri = intent.getStringExtra(AlarmActionReceiver.EXTRA_RINGTONE_URI) ?: AlarmActionReceiver.ALARM_NO_RINGTONE_URI
        val isVibrationEnabled = intent.getBooleanExtra(
            AlarmActionReceiver.EXTRA_IS_VIBRATION_ENABLED,
            AlarmActionReceiver.ALARM_NO_IS_VIBRATION_ENABLED
        )

        // Get the General Settings to determine if we're using 12 or 24 hour time,
        // then display the Notification and play the Alarm sound.
        CoroutineScope(Dispatchers.IO).launch {
            // Get the General Settings
            val generalSettingsRepository = GeneralSettingsRepository(applicationContext.generalSettingsDataStore)
            val generalSettings = try {
                async { generalSettingsRepository.generalSettingsFlow }.await().first()
            } catch (e: Exception) {
                // Flow was empty. Return GeneralSettings with defaults.
                GeneralSettings(GeneralSettingsRepository.DEFAULT_TIME_DISPLAY)
            }
            // Switch back to Main Thread for UI related work
            withContext(Dispatchers.Main) {
                // Create Notification
                val fullScreenNotification =
                    AlarmNotification.fullScreenNotification(
                        applicationContext,
                        alarmId,
                        alarmName,
                        alarmDateTime,
                        generalSettings.timeDisplay
                    )

                // Push Service to foreground and display Notification
                startForeground(alarmId, fullScreenNotification)

                // TODO: Check notification permission before sounding Alarm. If you don't,
                //  then the ringtone will sound without the notification.
                // Play Ringtone
                RingtonePlayerManager.startAlarmSound(applicationContext, ringtoneUri)

                // Start vibration
                if (isVibrationEnabled) {
                    VibrationController.startVibration(applicationContext)
                }
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

        // Stop vibration
        VibrationController.stopVibration(applicationContext)

        // Stop Service, which dismisses the Notification
        stopSelf()
    }
}
