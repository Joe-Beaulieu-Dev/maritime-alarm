package com.octrobi.lavalarm.core.recovery

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import com.octrobi.lavalarm.alarm.alarmexecution.AlarmRefreshReceiver
import com.octrobi.lavalarm.alarm.data.repository.AlarmDatabase
import com.octrobi.lavalarm.alarm.data.repository.AlarmRepository

object ForceStopRecoveryHandler {

    /**
     * Do not call with APIs >= 35. Indirectly determine if it was possible that the app was in a
     * Package Stopped state (Force Stopped) prior to the current run, and return whether or not
     * we should perform recovery operations.
     *
     * This function is only needed on APIs < 35 since direct methods of detecting a Force Stop were
     * not added until API 35. Unfortunately on APIs < 35 since there is no direct way to tell, it must be inferred.
     * This function gives a best guess, and defaults to true in scenarios where it is impossible to tell if
     * the app may have been Force Stopped. Therefore, although it detects indirectly, it is still reliable
     * since there will be no false negatives because it defaults to true in any scenario where a false
     * negative may occur.
     *
     * Detection is performed by taking advantage of the fact that this app schedules Alarms with
     * AlarmManger.setAlarmClock(), and you can see the very next Alarm scheduled this way via
     * AlarmManager.getNextAlarmClock(). Furthermore, you can see which app scheduled the Alarm that's
     * returned by calling AlarmManager.getNextAlarmClock()?.getShowIntent()?.getCreatorPackage(). This is
     * necessary since AlarmManager.getNextAlarmClock() will return AlarmClockInfo for the next Alarm even
     * if it's not from your app. If the function returns null, then there's no Alarms scheduled with
     * AlarmManger.setAlarmClock(). After checking the AlarmClockInfo we check if there's any enabled Alarms
     * in the database to make a determination as to whether or not a Force Stop may have occurred and if
     * recovery is necessary. Below is a breakdown of scenarios to illustrate the logic behind this function:
     *
     * Device is running an API < 35
     *   1) AlarmClockInfo is from a different app
     *     - There is no way to tell if the app was Force Stopped. Check the database for any enabled Alarms.
     *       - If there are enabled Alarms in the database, return true. The app MAY have been Force Stopped,
     *         and since the database has enabled Alarms we should refresh them to ensure reliability.
     *       - If there are no enabled Alarms in the database, return false. The app MAY have been Force
     *         Stopped, but since the database has no enabled Alarms we have nothing to refresh.
     *   2) AlarmClockInfo is from this app
     *     - The app was definitely not Force Stopped, return false. Force Stopping an app clears all of its
     *       Alarm data with AlarmManager, so the fact that we have an Alarm scheduled shows that no Force Stop
     *       occurred. Because of this, checking the database for enabled Alarms is unnecessary.
     *   3) AlarmClockInfo is null
     *     - There is no way to tell if the app was Force Stopped. Treat this the same as Scenario 1 where
     *       the AlarmClockInfo is from another app.
     *
     * Device is running an API >= 35
     *  1) This function is not needed and returns true by default to err on the side of caution. Force Stop Recovery
     *     is handled directly in AlarmRefreshReceiver on APIs >= 35. This is because starting with API 35, the system
     *     will send Intent.ACTION_LOCKED_BOOT_COMPLETED and Intent.ACTION_BOOT_COMPLETED (although the documentation
     *     only mentions Intent.ACTION_BOOT_COMPLETED). This is somewhat unfortunate as these Intent actions are both
     *     already used in other scenarios, so reusing them makes them ambiguous. However, since this app performs the
     *     same action (Alarm refresh) on both device boot and Force Stop Recovery, this is fine. Furthermore, API 35
     *     also introduced ApplicationStartInfo.wasForceStopped() so you can query for Force Stops on demand, rather
     *     than reacting to an Intent action.
     *
     * For details on the new Force Stop Recovery options and behavior introduced in API 35, see:
     *   - https://developer.android.com/about/versions/15/behavior-changes-all#enhanced-stop-states
     *
     * @param context used to perform various functions related to the System
     *
     * @return true if the app may have been previously Force Stopped and needs recovery, false if otherwise.
     *         Defaults to true if called on APIs >= 35 to err on the side of caution. Do not call on APIs >= 35.
     */
    suspend fun shouldPerformForceStopRecoveryPreApi35(context: Context): Boolean =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            val nextAlarmCreator = alarmManager.nextAlarmClock?.showIntent?.creatorPackage
            val myPackageName = context.packageName

            if (nextAlarmCreator != myPackageName) {
                // Either their are no "Alarm Clock" Alarms scheduled with AlarmManager, or the next "Alarm Clock"
                // Alarm is from another app. Check to see if there's any enabled Alarms in the database. If so, then
                // this is an indication that the app MIGHT have been Forced Stopped. Return true to err on the side
                // of caution in order to ensure reliability. If there's no enabled Alarms in the database, then it's
                // still possible the app MIGHT have been Force Stopped, but if there's no enabled Alarms in the database
                // then there's nothing to refresh so just return false. Returning false in this scenario is desired
                // as this function is utilized to determine whether or not action needs to be taken in the form of an
                // Alarm refresh.
                val alarmRepository = AlarmRepository(
                    AlarmDatabase
                        .getDatabase(context.createDeviceProtectedStorageContext())
                        .alarmDao()
                )
                alarmRepository.getAllEnabledAlarms().isNotEmpty()
            } else {
                // The next upcoming Alarm scheduled with AlarmManager is from this app,
                // therefore it was not Force Stopped.
                false
            }
        } else {
            // This function should not be used on APIs >= 35.
            // On APIs >= 35, Force Stop Recovery is handled directly in AlarmRefreshReceiver
            // in response to receiving Intent.ACTION_LOCKED_BOOT_COMPLETED.
            // On APIs < 35 however, this Intent action has nothing to do with Force Stop Recovery.
            // Since Force Stop Recovery is handled elsewhere on APIs >= 35, return true here because
            // this function should always default to true in order to ensure reliability.
            true
        }

    /**
     * Perform any recovery operation necessary after a Force Stop.
     *
     * @param context used to perform various functions related to the System
     */
    fun performForceStopRecoveryPreApi35(context: Context) {
        context.sendBroadcast(
            Intent(context, AlarmRefreshReceiver::class.java).apply {
                action = AlarmRefreshReceiver.ACTION_FORCE_STOP_RECOVERY_PRE_API_35
            }
        )
    }
}
