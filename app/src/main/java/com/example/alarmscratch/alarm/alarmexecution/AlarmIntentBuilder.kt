package com.example.alarmscratch.alarm.alarmexecution

import android.content.Context
import android.content.Intent
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData

object AlarmIntentBuilder {

    /**
     * Creates an Intent for executing Alarms. This Intent contains all the properties of AlarmExecutionData.
     *
     * @param context used for Intent creation
     * @param alarmExecutionData execution data for the Alarm
     *
     * @return Intent for executing Alarms
     */
    fun executeAlarmIntent(context: Context, alarmExecutionData: AlarmExecutionData): Intent =
        Intent(context, AlarmActionReceiver::class.java).apply {
            // Action
            action = AlarmActionReceiver.ACTION_EXECUTE_ALARM
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmExecutionData.id)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_NAME, alarmExecutionData.name)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_EXECUTION_DATE_TIME, alarmExecutionData.executionDateTime.toString())
            putExtra(AlarmActionReceiver.EXTRA_REPEATING_DAYS, alarmExecutionData.encodedRepeatingDays)
            putExtra(AlarmActionReceiver.EXTRA_RINGTONE_URI, alarmExecutionData.ringtoneUri)
            putExtra(AlarmActionReceiver.EXTRA_IS_VIBRATION_ENABLED, alarmExecutionData.isVibrationEnabled)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_SNOOZE_DURATION, alarmExecutionData.snoozeDuration)
        }

    /**
     * Creates an Intent for snoozing Alarms. This Intent does not contain all the properties of AlarmExecutionData.
     *
     * @param context used for Intent creation
     * @param alarmExecutionData execution data for the Alarm
     *
     * @return Intent for snoozing Alarms
     */
    fun snoozeAlarmIntent(context: Context, alarmExecutionData: AlarmExecutionData): Intent =
        Intent(context, AlarmActionReceiver::class.java).apply {
            // Action
            action = AlarmActionReceiver.ACTION_SNOOZE_AND_RESCHEDULE_ALARM
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmExecutionData.id)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_NAME, alarmExecutionData.name)
            putExtra(AlarmActionReceiver.EXTRA_REPEATING_DAYS, alarmExecutionData.encodedRepeatingDays)
            putExtra(AlarmActionReceiver.EXTRA_RINGTONE_URI, alarmExecutionData.ringtoneUri)
            putExtra(AlarmActionReceiver.EXTRA_IS_VIBRATION_ENABLED, alarmExecutionData.isVibrationEnabled)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_SNOOZE_DURATION, alarmExecutionData.snoozeDuration)
        }

    /**
     * Creates an Intent for dismissing Alarms. This Intent does not contain all the properties of AlarmExecutionData.
     *
     * @param context used for Intent creation
     * @param alarmExecutionData execution data for the Alarm
     *
     * @return Intent for dismissing Alarms
     */
    fun dismissAlarmIntent(context: Context, alarmExecutionData: AlarmExecutionData): Intent =
        Intent(context, AlarmActionReceiver::class.java).apply {
            // Action
            action = AlarmActionReceiver.ACTION_DISMISS_ALARM
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmExecutionData.id)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_EXECUTION_DATE_TIME, alarmExecutionData.executionDateTime.toString())
            putExtra(AlarmActionReceiver.EXTRA_REPEATING_DAYS, alarmExecutionData.encodedRepeatingDays)
        }
}
