package com.example.alarmscratch.alarm.ui.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.alarmexecution.AlarmNotificationActionReceiver
import com.example.alarmscratch.alarm.alarmexecution.AlarmReceiver
import com.example.alarmscratch.alarm.ui.fullscreenalert.FullScreenAlarmActivity
import com.example.alarmscratch.core.extension.toNotificationDateTimeString
import com.example.alarmscratch.core.ringtone.RingtonePlayerManager
import java.time.LocalDateTime

class AlarmNotificationService(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID_ALARM_NOTIFICATION = "channel_id_alarm_notification"
    }

    fun showNotification(alarmId: Int, alarmName: String, alarmDateTime: String, ringtoneUriString: String) {
        val formattedDateTime = try {
            LocalDateTime.parse(alarmDateTime).toNotificationDateTimeString(context)
        } catch (e: Exception) {
            context.getString(R.string.default_alarm_time)
        }

        val dismissAlarmIntent = Intent(context, AlarmNotificationActionReceiver::class.java).apply {
            action = AlarmNotificationActionReceiver.ACTION_DISMISS_ALARM
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
        }

        val dismissAlarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            dismissAlarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissAlarmAction = Notification.Action.Builder(
            Icon.createWithResource(context, R.drawable.ic_alarm_dismiss_24dp),
            context.getString(R.string.dismiss_alarm),
            dismissAlarmPendingIntent
        ).build()

        val notification = Notification.Builder(context, CHANNEL_ID_ALARM_NOTIFICATION)
            .setSmallIcon(R.drawable.ic_alarm_24dp)
            .setContentTitle(alarmName)
            .setContentText(formattedDateTime)
            .setCategory(Notification.CATEGORY_ALARM)
            .addAction(dismissAlarmAction)
            .build()

        notificationManager.notify(alarmId, notification)

        // TODO: Check notification permission before sounding Alarm. If you don't,
        //  then the ringtone will sound without the notification.
        RingtonePlayerManager.startAlarmSound(context, ringtoneUriString)
    }

    fun showFullScreenNotification(alarmId: Int, alarmName: String, alarmDateTime: String) {
        // This shows up in the Status Bar notification, but not in the full screen alert
        val notificationDateTimeString = try {
            LocalDateTime.parse(alarmDateTime).toNotificationDateTimeString(context)
        } catch (e: Exception) {
            context.getString(R.string.default_alarm_time)
        }

        val intent = Intent(context, FullScreenAlarmActivity::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_NAME, alarmName)
            putExtra(AlarmReceiver.EXTRA_ALARM_DATE_TIME, alarmDateTime)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification.Builder(context, CHANNEL_ID_ALARM_NOTIFICATION)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(alarmName)
            .setContentText(notificationDateTimeString)
            .setFullScreenIntent(pendingIntent, true)
            .build()

        notificationManager.notify(alarmId, notification)
    }
}
