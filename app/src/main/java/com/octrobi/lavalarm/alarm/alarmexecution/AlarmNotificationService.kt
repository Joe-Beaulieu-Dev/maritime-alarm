package com.octrobi.lavalarm.alarm.alarmexecution

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.alarm.data.model.Alarm
import com.octrobi.lavalarm.alarm.data.model.AlarmExecutionData
import com.octrobi.lavalarm.alarm.data.repository.AlarmDatabase
import com.octrobi.lavalarm.alarm.data.repository.AlarmRepository
import com.octrobi.lavalarm.alarm.ui.fullscreenalert.FullScreenAlarmActivity
import com.octrobi.lavalarm.alarm.ui.fullscreenalert.FullScreenAlarmButton
import com.octrobi.lavalarm.alarm.ui.notification.AlarmNotification
import com.octrobi.lavalarm.core.constant.actionPackageName
import com.octrobi.lavalarm.core.extension.LocalDateTimeUtil
import com.octrobi.lavalarm.core.extension.getSerializableExtraSafe
import com.octrobi.lavalarm.core.ringtone.RingtonePlayerManager
import com.octrobi.lavalarm.core.ui.notificationcheck.AppNotificationChannel
import com.octrobi.lavalarm.core.ui.permission.Permission
import com.octrobi.lavalarm.core.util.NotificationChannelUtil
import com.octrobi.lavalarm.core.util.PermissionUtil
import com.octrobi.lavalarm.settings.data.model.GeneralSettings
import com.octrobi.lavalarm.settings.data.repository.AlarmDefaultsRepository
import com.octrobi.lavalarm.settings.data.repository.GeneralSettingsRepository
import com.octrobi.lavalarm.settings.data.repository.generalSettingsDataStore
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
        const val ACTION_DISPLAY_ALARM_NOTIFICATION = "${actionPackageName}DISPLAY_ALARM_NOTIFICATION"
        const val ACTION_DISMISS_ALARM_NOTIFICATION = "${actionPackageName}DISMISS_ALARM_NOTIFICATION"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_DISPLAY_ALARM_NOTIFICATION ->
                displayAlarmNotification(intent)
            ACTION_DISMISS_ALARM_NOTIFICATION ->
                dismissAlarmNotification(intent)
        }

        return START_NOT_STICKY
    }

    /*
     * Display
     */

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

            val isAlarmNotificationEnabled =
                NotificationChannelUtil.isNotificationChannelEnabled(this@AlarmNotificationService, AppNotificationChannel.Alarm)

            // POST_NOTIFICATIONS permission requires API 33 (TIRAMISU)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val isNotificationPermissionGranted =
                    PermissionUtil.isPermissionGranted(this@AlarmNotificationService, Permission.PostNotifications)

                logicGateNotificationLaunch(
                    { isNotificationPermissionGranted && isAlarmNotificationEnabled },
                    alarmExecutionData
                )
            } else {
                logicGateNotificationLaunch({ isAlarmNotificationEnabled }, alarmExecutionData)
            }
        }
    }

    private suspend fun logicGateNotificationLaunch(gateLogic: () -> Boolean, alarmExecutionData: AlarmExecutionData) {
        // This function seems a bit over the top to have, but the version-based
        // permission/notification logic got a bit ugly and this makes it look a little nicer.
        if (gateLogic()) {
            launchNotification(alarmExecutionData)
        } else {
            val alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(this).alarmDao())
            val alarm = alarmRepository.getAlarm(alarmExecutionData.id)
            AlarmScheduler.disableOrRescheduleAlarm(applicationContext, alarmRepository, alarm)
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
            finishFullScreenAlarmFlow()
            // Disable/reschedule Alarm
            AlarmScheduler.disableOrRescheduleAlarm(applicationContext, alarmRepo, notificationAlarm)
        }
    }

    /*
     * Dismiss
     */

    private fun dismissAlarmNotification(intent: Intent) {
        // TODO: Come up with a better default than just AlarmActionOrigin.NOTIFICATION
        val alarmActionOrigin = intent.getSerializableExtraSafe(
            AlarmActionReceiver.EXTRA_ALARM_ACTION_ORIGIN,
            AlarmActionOrigin::class.java
        ) ?: AlarmActionOrigin.NOTIFICATION

        // Dismiss Full Screen Notification
        if (alarmActionOrigin == AlarmActionOrigin.NOTIFICATION) {
            finishFullScreenAlarmFlow()
        } else {
            showPostAlarmConfirmation(intent)
        }

        // Stop Ringtone
        RingtonePlayerManager.stopAlarmSound()

        // Stop Vibration
        VibrationController.stopVibration(applicationContext)

        // Release WakeLock
        WakeLockManager.releaseWakeLock()

        // Stop Service, which dismisses the StatusBar Notification
        stopSelf()
    }

    private fun showPostAlarmConfirmation(intent: Intent) {
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

        // Create Intent and send Broadcast
        val showPostAlarmConfirmationIntent = Intent().apply {
            // Action
            action = FullScreenAlarmActivity.ACTION_SHOW_POST_ALARM_CONFIRMATION
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_FULL_SCREEN_ALARM_BUTTON, fullScreenAlarmButton)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_SNOOZE_DURATION, snoozeDuration)

            // On devices running API 34+, it is required to call setPackage() on implicit Intents
            // that are not exported, and are to be used by an application's internal components.
            setPackage(this@AlarmNotificationService.packageName)
        }
        applicationContext.sendBroadcast(showPostAlarmConfirmationIntent)
    }

    private fun finishFullScreenAlarmFlow() {
        val finishFullScreenAlarmFlowIntent = Intent().apply {
            action = FullScreenAlarmActivity.ACTION_FINISH_FULL_SCREEN_ALARM_FLOW
            // On devices running API 34+, it is required to call setPackage() on implicit Intents
            // that are not exported, and are to be used by an application's internal components.
            setPackage(this@AlarmNotificationService.packageName)
        }
        applicationContext.sendBroadcast(finishFullScreenAlarmFlowIntent)
    }

    override fun onDestroy() {
        // Cancel coroutines
        job.cancel()
    }
}
