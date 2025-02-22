package com.example.alarmscratch.alarm.alarmexecution

import android.content.Context
import android.content.Intent
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData

object AlarmIntentBuilder {

    /*
     * Execute
     */

    /**
     * Creates an Intent for executing Alarms. This Intent contains all the properties of AlarmExecutionData.
     *
     * @param context used for Intent creation
     * @param alarmExecutionData execution data for the Alarm
     *
     * @return Intent for executing Alarms
     */
    fun executeAlarmIntent(context: Context, alarmExecutionData: AlarmExecutionData): Intent {
        val name = alarmExecutionData.name.ifBlank { context.getString(R.string.default_alarm_name) }

        return Intent(context, AlarmActionReceiver::class.java).apply {
            // Action
            action = AlarmActionReceiver.ACTION_EXECUTE_ALARM
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmExecutionData.id)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_NAME, name)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_EXECUTION_DATE_TIME, alarmExecutionData.executionDateTime.toString())
            putExtra(AlarmActionReceiver.EXTRA_REPEATING_DAYS, alarmExecutionData.encodedRepeatingDays)
            putExtra(AlarmActionReceiver.EXTRA_RINGTONE_URI, alarmExecutionData.ringtoneUri)
            putExtra(AlarmActionReceiver.EXTRA_IS_VIBRATION_ENABLED, alarmExecutionData.isVibrationEnabled)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_SNOOZE_DURATION, alarmExecutionData.snoozeDuration)
        }
    }

    /*
     * Snooze
     */

    /**
     * Creates an Intent for snoozing Alarms via the Alarm Notification.
     * This Intent does not contain all the properties of AlarmExecutionData.
     *
     * @param context used for Intent creation
     * @param alarmExecutionData execution data for the Alarm
     *
     * @return Intent for snoozing Alarms via the Alarm Notification
     */
    fun snoozeAlarmFromNotification(context: Context, alarmExecutionData: AlarmExecutionData): Intent =
        snoozeAlarmIntent(context, AlarmActionOrigin.NOTIFICATION, alarmExecutionData)

    /**
     * Creates an Intent for snoozing Alarms via the Full Screen Alarm.
     * This Intent does not contain all the properties of AlarmExecutionData.
     *
     * @param context used for Intent creation
     * @param alarmExecutionData execution data for the Alarm
     *
     * @return Intent for snoozing Alarms via the Full Screen Alarm
     */
    fun snoozeAlarmFromFullScreen(context: Context, alarmExecutionData: AlarmExecutionData): Intent =
        snoozeAlarmIntent(context, AlarmActionOrigin.FULL_SCREEN, alarmExecutionData)

    /**
     * Creates an Intent for snoozing Alarms from either the Alarm Notification or the Full Screen Alarm,
     * depending on the [alarmActionOrigin]. This Intent does not contain all the properties of AlarmExecutionData.
     *
     * @param context used for Intent creation
     * @param alarmActionOrigin the origin of the snooze action (ex: Notification, Full Screen Alarm)
     * @param alarmExecutionData execution data for the Alarm
     *
     * @return Intent for snoozing Alarms from either the Alarm Notification or the Full Screen Alarm
     */
    private fun snoozeAlarmIntent(
        context: Context,
        alarmActionOrigin: AlarmActionOrigin,
        alarmExecutionData: AlarmExecutionData
    ): Intent {
        val name = alarmExecutionData.name.ifBlank { context.getString(R.string.default_alarm_name) }

        return Intent(context, AlarmActionReceiver::class.java).apply {
            // Action
            action = AlarmActionReceiver.ACTION_SNOOZE_AND_RESCHEDULE_ALARM
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmExecutionData.id)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_NAME, name)
            putExtra(AlarmActionReceiver.EXTRA_REPEATING_DAYS, alarmExecutionData.encodedRepeatingDays)
            putExtra(AlarmActionReceiver.EXTRA_RINGTONE_URI, alarmExecutionData.ringtoneUri)
            putExtra(AlarmActionReceiver.EXTRA_IS_VIBRATION_ENABLED, alarmExecutionData.isVibrationEnabled)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_SNOOZE_DURATION, alarmExecutionData.snoozeDuration)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ACTION_ORIGIN, alarmActionOrigin)
        }
    }

    /*
     * Dismiss
     */

    /**
     * Creates an Intent for dismissing Alarms via the Alarm Notification.
     * This Intent does not contain all the properties of AlarmExecutionData.
     *
     * @param context used for Intent creation
     * @param alarmExecutionData execution data for the Alarm
     *
     * @return Intent for dismissing Alarms via the Alarm Notification
     */
    fun dismissAlarmFromNotification(context: Context, alarmExecutionData: AlarmExecutionData): Intent =
        dismissAlarmIntent(context, AlarmActionOrigin.NOTIFICATION, alarmExecutionData)

    /**
     * Creates an Intent for dismissing Alarms via the Full Screen Alarm.
     * This Intent does not contain all the properties of AlarmExecutionData.
     *
     * @param context used for Intent creation
     * @param alarmExecutionData execution data for the Alarm
     *
     * @return Intent for dismissing Alarms via the Full Screen Alarm
     */
    fun dismissAlarmFromFullScreen(context: Context, alarmExecutionData: AlarmExecutionData): Intent =
        dismissAlarmIntent(context, AlarmActionOrigin.FULL_SCREEN, alarmExecutionData)

    /**
     * Creates an Intent for dismissing Alarms from either the Alarm Notification or the Full Screen Alarm,
     * depending on the [alarmActionOrigin]. This Intent does not contain all the properties of AlarmExecutionData.
     *
     * @param context used for Intent creation
     * @param alarmActionOrigin the origin of the dismiss action (ex: Notification, Full Screen Alarm)
     * @param alarmExecutionData execution data for the Alarm
     *
     * @return Intent for dismissing Alarms from either the Alarm Notification or the Full Screen Alarm
     */
    private fun dismissAlarmIntent(
        context: Context,
        alarmActionOrigin: AlarmActionOrigin,
        alarmExecutionData: AlarmExecutionData
    ): Intent =
        Intent(context, AlarmActionReceiver::class.java).apply {
            // Action
            action = AlarmActionReceiver.ACTION_DISMISS_ALARM
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmExecutionData.id)
            putExtra(AlarmActionReceiver.EXTRA_REPEATING_DAYS, alarmExecutionData.encodedRepeatingDays)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ACTION_ORIGIN, alarmActionOrigin)
        }
}
