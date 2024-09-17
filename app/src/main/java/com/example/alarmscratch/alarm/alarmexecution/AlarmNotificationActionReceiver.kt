package com.example.alarmscratch.alarm.alarmexecution

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.alarm.ui.fullscreenalert.FullScreenAlarmActivity
import com.example.alarmscratch.core.ringtone.RingtonePlayerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlarmNotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DISMISS_ALARM = "action_dismiss_alarm"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val alarmId = intent.getIntExtra(AlarmReceiver.EXTRA_ALARM_ID, AlarmReceiver.ALARM_NO_ID)

            // If there's no Alarm ID then something's wrong. Do not handle event.
            if (alarmId == AlarmReceiver.ALARM_NO_ID) return

            if (intent.action == ACTION_DISMISS_ALARM) {
                dismissAlarm(context, alarmId)
            }
        }
    }

    /**
     * Dismisses the Alarm by performing the following actions:
     *   1) Dismiss the Alarm Notification from the Status Bar
     *   2) Disable the Alarm in the Database
     *   3) Stop Ringtone playback
     *   4) Send a Broadcast to finish the Full Screen Intent Activity
     *
     * @param context Context used to get handles to things
     * @param alarmId ID of the Alarm to be dismissed
     */
    private fun dismissAlarm(context: Context, alarmId: Int) {
        // Dismiss Notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(alarmId)

        // Disable Alarm
        val alarmRepo = AlarmRepository(
            AlarmDatabase
                .getDatabase(context.createDeviceProtectedStorageContext())
                .alarmDao()
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val alarm = async { alarmRepo.getAlarmFlow(alarmId) }.await().first()
                alarmRepo.updateAlarm(alarm.copy(enabled = false))
            } catch (e: Exception) {
                // Flow was empty. Not doing anything with this. Just don't crash.
            }
        }

        // Stop Ringtone playback
        RingtonePlayerManager.stopAlarmSound()

        // Send Broadcast to finish the Full Screen Intent Activity
        val dismissFullScreenAlertIntent = Intent().apply {
            action = FullScreenAlarmActivity.ACTION_FINISH_FULL_SCREEN_ALARM_ACTIVITY
        }
        context.sendBroadcast(dismissFullScreenAlertIntent)
    }
}
