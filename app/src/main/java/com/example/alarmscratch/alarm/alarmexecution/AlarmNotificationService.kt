package com.example.alarmscratch.alarm.alarmexecution

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.alarm.ui.fullscreenalert.FullScreenAlarmActivity
import com.example.alarmscratch.alarm.ui.notification.AlarmNotification
import com.example.alarmscratch.alarm.util.AlarmUtil
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.isRepeating
import com.example.alarmscratch.core.extension.toAlarmExecutionData
import com.example.alarmscratch.core.ringtone.RingtonePlayerManager
import com.example.alarmscratch.core.ui.permission.Permission
import com.example.alarmscratch.core.util.PermissionUtil
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
            // If there is already an Alarm Notification up, dismiss the Alarm associated with it.
            // The call to startForeground() below will take care of cancelling the Notification
            // even if the ID is different.
            dismissPreviousAlarmIfActive()

            // POST_NOTIFICATIONS permission requires API 33 (TIRAMISU)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (PermissionUtil.isPermissionGranted(this@AlarmNotificationService, Permission.PostNotifications)) {
                    // TODO: Gate with Notification status check
                    launchNotification(alarmExecutionData)
                } else {
                    val alarmRepository = AlarmRepository(
                        AlarmDatabase.getDatabase(this@AlarmNotificationService).alarmDao()
                    )
                    val alarm = alarmRepository.getAlarm(alarmExecutionData.id)
                    disableOrRescheduleAlarm(alarmRepository, alarm)
                }
            } else {
                // TODO: Gate with Notification status check
                launchNotification(alarmExecutionData)
            }
        }
    }

    private suspend fun launchNotification(alarmExecutionData: AlarmExecutionData) {
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
        startForeground(alarmExecutionData.id, fullScreenNotification)

        // Play Ringtone
        RingtonePlayerManager.startAlarmSound(applicationContext, alarmExecutionData.ringtoneUri)

        // Start Vibration
        if (alarmExecutionData.isVibrationEnabled) {
            VibrationController.startVibration(applicationContext)
        }
    }

    private suspend fun dismissPreviousAlarmIfActive() {
        // Get all Notifications
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val allNotifications = notificationManager.activeNotifications
        // Get all Alarms
        val alarmRepo = AlarmRepository(AlarmDatabase.getDatabase(this).alarmDao())
        val allAlarms = alarmRepo.getAllAlarms()

        // Get Alarm that currently has a Notification
        val notificationAlarm: Alarm? = allAlarms.firstOrNull { alarm ->
            allNotifications.firstOrNull { notification -> notification.id == alarm.id } != null
        }

        // If there's an Active Alarm, dismiss the Full Screen Notification and disable/reschedule the Alarm
        if (notificationAlarm != null) {
            // Dismiss Full Screen Notification
            finishFullScreenAlarmActivity()
            // Disable/reschedule Alarm
            disableOrRescheduleAlarm(alarmRepo, notificationAlarm)
        }
    }

    private suspend fun disableOrRescheduleAlarm(alarmRepository: AlarmRepository, alarm: Alarm) {
        // Dismiss/reschedule Alarm
        if (alarm.isRepeating()) {
            // Calculate the next time the repeating Alarm should execute
            val nextDateTime = AlarmUtil.nextRepeatingDateTime(
                alarm.dateTime,
                alarm.weeklyRepeater
            )
            // Dismiss Alarm and update with nextDateTime
            alarmRepository.dismissAndRescheduleRepeating(alarm.id, nextDateTime)
            // Reschedule Alarm with nextDateTime
            AlarmScheduler.scheduleAlarm(
                applicationContext,
                alarm.toAlarmExecutionData().copy(executionDateTime = nextDateTime)
            )
        } else {
            // Dismiss non-repeating Alarm
            alarmRepository.dismissAlarm(alarm.id)
        }
    }

    private fun dismissAlarmNotification() {
        // Dismiss Full Screen Notification
        finishFullScreenAlarmActivity()

        // Stop Ringtone
        RingtonePlayerManager.stopAlarmSound()

        // Stop Vibration
        VibrationController.stopVibration(applicationContext)

        // Release WakeLock
        WakeLockManager.releaseWakeLock()

        // Stop Service, which dismisses the StatusBar Notification
        stopSelf()
    }

    private fun finishFullScreenAlarmActivity() {
        val dismissFullScreenAlertIntent = Intent().apply {
            action = FullScreenAlarmActivity.ACTION_FINISH_FULL_SCREEN_ALARM_ACTIVITY
            // On devices running API 34+, it is required to call setPackage() on implicit Intents
            // that are not exported, and are to be used by an application's internal components.
            setPackage(this@AlarmNotificationService.packageName)
        }
        applicationContext.sendBroadcast(dismissFullScreenAlertIntent)
    }

    override fun onDestroy() {
        // Cancel coroutines
        job.cancel()
    }
}
