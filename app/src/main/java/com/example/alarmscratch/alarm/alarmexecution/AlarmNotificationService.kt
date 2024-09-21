package com.example.alarmscratch.alarm.alarmexecution

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.ui.fullscreenalert.FullScreenAlarmActivity
import com.example.alarmscratch.alarm.ui.notification.AlarmNotification
import com.example.alarmscratch.core.ringtone.RingtonePlayerManager

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

        // TODO: Check this before PR
        return START_NOT_STICKY
//        return super.onStartCommand(intent, flags, startId)
    }

    private fun displayAlarmNotification(intent: Intent) {
        val alarmId = intent.getIntExtra(AlarmActionReceiver.EXTRA_ALARM_ID, AlarmActionReceiver.ALARM_NO_ID)
        val alarmName = intent.getStringExtra(AlarmActionReceiver.EXTRA_ALARM_NAME) ?: getString(R.string.default_alarm_name)
        val alarmDateTime = intent.getStringExtra(AlarmActionReceiver.EXTRA_ALARM_DATE_TIME) ?: getString(R.string.default_alarm_time)
        val ringtoneUri = intent.getStringExtra(AlarmActionReceiver.EXTRA_RINGTONE_URI) ?: AlarmActionReceiver.ALARM_NO_RINGTONE_URI

        // Create Notification
        val fullScreenNotification =
            AlarmNotification.fullScreenNotification(
                applicationContext,
                alarmId,
                alarmName,
                alarmDateTime
            )

        // Push Service to foreground and display Notification
        startForeground(alarmId, fullScreenNotification)

        // TODO: Check notification permission before sounding Alarm. If you don't,
        //  then the ringtone will sound without the notification.
        // Play Ringtone
        RingtonePlayerManager.startAlarmSound(applicationContext, ringtoneUri)
    }

    private fun dismissAlarmNotification() {
        // Finish FullScreenAlarmActivity
        val dismissFullScreenAlertIntent = Intent().apply {
            action = FullScreenAlarmActivity.ACTION_FINISH_FULL_SCREEN_ALARM_ACTIVITY
        }
        applicationContext.sendBroadcast(dismissFullScreenAlertIntent)

        // Stop Ringtone
        RingtonePlayerManager.stopAlarmSound()

        // Stop Service, which dismisses the Notification
        stopSelf()
    }
}
