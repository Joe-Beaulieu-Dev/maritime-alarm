package com.octrobi.lavalarm.core.recovery

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.octrobi.lavalarm.alarm.alarmexecution.AlarmRefreshReceiver
import com.octrobi.lavalarm.alarm.data.model.Alarm
import com.octrobi.lavalarm.alarm.data.model.WeeklyRepeater
import com.octrobi.lavalarm.alarm.data.repository.AlarmRepository
import com.octrobi.lavalarm.core.extension.LocalDateTimeUtil
import com.octrobi.lavalarm.core.util.BuildVersionUtil
import io.mockk.EqMatcher
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ForceStopRecoveryHandlerTest {

    // Alarm
    private val enabledAlarm = Alarm(
        id = 1,
        name = "name",
        enabled = true,
        dateTime = LocalDateTimeUtil.nowTruncated(),
        weeklyRepeater = WeeklyRepeater(),
        ringtoneUri = "ringtoneUri",
        isVibrationEnabled = false,
        snoozeDateTime = null,
        snoozeDuration = 10
    )
    private val enabledAlarmList: List<Alarm> = listOf(enabledAlarm)

    /*
     * shouldPerformForceStopRecoveryPreApi35
     */

    // Next Alarm different app
    @Test
    fun shouldPerformForceStopRecoveryPreApi35_returnsTrue_nextAlarmFromDifferentApp_enabledAlarmsInDb() = runTest {
        // Arrange
        val myPackageName = "my.package"
        val otherPackageName = "other.package"
        val pendingIntent = mockk<PendingIntent> {
            every { creatorPackage } returns otherPackageName
        }
        val alarmClockInfo = mockk<AlarmManager.AlarmClockInfo> {
            every { showIntent } returns pendingIntent
        }
        val alarmManager = mockk<AlarmManager> {
            every { nextAlarmClock } returns alarmClockInfo
        }
        val context = mockk<Context> {
            every { getSystemService(AlarmManager::class.java) } returns alarmManager
            every { packageName } returns myPackageName
        }
        val alarmRepository = mockk<AlarmRepository> {
            coEvery { getAllEnabledAlarms() } returns enabledAlarmList
        }

        // Act & Assert
        mockkObject(BuildVersionUtil) {
            every { BuildVersionUtil.getCurrentSkdInt() } returns Build.VERSION_CODES.VANILLA_ICE_CREAM - 1

            val shouldPerformRecovery = ForceStopRecoveryHandler.shouldPerformForceStopRecoveryPreApi35(context, alarmRepository)
            assertTrue(shouldPerformRecovery)
        }
    }

    @Test
    fun shouldPerformForceStopRecoveryPreApi35_returnsFalse_nextAlarmFromDifferentApp_noEnabledAlarmsInDb() = runTest {
        // Arrange
        val myPackageName = "my.package"
        val otherPackageName = "other.package"
        val pendingIntent = mockk<PendingIntent> {
            every { creatorPackage } returns otherPackageName
        }
        val alarmClockInfo = mockk<AlarmManager.AlarmClockInfo> {
            every { showIntent } returns pendingIntent
        }
        val alarmManager = mockk<AlarmManager> {
            every { nextAlarmClock } returns alarmClockInfo
        }
        val context = mockk<Context> {
            every { getSystemService(AlarmManager::class.java) } returns alarmManager
            every { packageName } returns myPackageName
        }
        val alarmRepository = mockk<AlarmRepository> {
            coEvery { getAllEnabledAlarms() } returns emptyList()
        }

        // Act & Assert
        mockkObject(BuildVersionUtil) {
            every { BuildVersionUtil.getCurrentSkdInt() } returns Build.VERSION_CODES.VANILLA_ICE_CREAM - 1

            val shouldPerformRecovery = ForceStopRecoveryHandler.shouldPerformForceStopRecoveryPreApi35(context, alarmRepository)
            assertFalse(shouldPerformRecovery)
        }
    }

    // Next Alarm null
    @Test
    fun shouldPerformForceStopRecoveryPreApi35_returnsTrue_nextAlarmNull_enabledAlarmsInDb() = runTest {
        // Arrange
        val myPackageName = "my.package"
        val otherPackageName = null
        val pendingIntent = mockk<PendingIntent> {
            every { creatorPackage } returns otherPackageName
        }
        val alarmClockInfo = mockk<AlarmManager.AlarmClockInfo> {
            every { showIntent } returns pendingIntent
        }
        val alarmManager = mockk<AlarmManager> {
            every { nextAlarmClock } returns alarmClockInfo
        }
        val context = mockk<Context> {
            every { getSystemService(AlarmManager::class.java) } returns alarmManager
            every { packageName } returns myPackageName
        }
        val alarmRepository = mockk<AlarmRepository> {
            coEvery { getAllEnabledAlarms() } returns enabledAlarmList
        }

        // Act & Assert
        mockkObject(BuildVersionUtil) {
            every { BuildVersionUtil.getCurrentSkdInt() } returns Build.VERSION_CODES.VANILLA_ICE_CREAM - 1

            val shouldPerformRecovery = ForceStopRecoveryHandler.shouldPerformForceStopRecoveryPreApi35(context, alarmRepository)
            assertTrue(shouldPerformRecovery)
        }
    }

    @Test
    fun shouldPerformForceStopRecoveryPreApi35_returnsFalse_nextAlarmNull_noEnabledAlarmsInDb() = runTest {
        // Arrange
        val myPackageName = "my.package"
        val otherPackageName = null
        val pendingIntent = mockk<PendingIntent> {
            every { creatorPackage } returns otherPackageName
        }
        val alarmClockInfo = mockk<AlarmManager.AlarmClockInfo> {
            every { showIntent } returns pendingIntent
        }
        val alarmManager = mockk<AlarmManager> {
            every { nextAlarmClock } returns alarmClockInfo
        }
        val context = mockk<Context> {
            every { getSystemService(AlarmManager::class.java) } returns alarmManager
            every { packageName } returns myPackageName
        }
        val alarmRepository = mockk<AlarmRepository> {
            coEvery { getAllEnabledAlarms() } returns emptyList()
        }

        // Act & Assert
        mockkObject(BuildVersionUtil) {
            every { BuildVersionUtil.getCurrentSkdInt() } returns Build.VERSION_CODES.VANILLA_ICE_CREAM - 1

            val shouldPerformRecovery = ForceStopRecoveryHandler.shouldPerformForceStopRecoveryPreApi35(context, alarmRepository)
            assertFalse(shouldPerformRecovery)
        }
    }

    // Next Alarm this app
    @Test
    fun shouldPerformForceStopRecoveryPreApi35_returnsFalse_nextAlarmFromThisApp_enabledAlarmsInDb() = runTest {
        // Arrange
        val myPackageName = "my.package"
        val pendingIntent = mockk<PendingIntent> {
            every { creatorPackage } returns myPackageName
        }
        val alarmClockInfo = mockk<AlarmManager.AlarmClockInfo> {
            every { showIntent } returns pendingIntent
        }
        val alarmManager = mockk<AlarmManager> {
            every { nextAlarmClock } returns alarmClockInfo
        }
        val context = mockk<Context> {
            every { getSystemService(AlarmManager::class.java) } returns alarmManager
            every { packageName } returns myPackageName
        }
        val alarmRepository = mockk<AlarmRepository> {
            coEvery { getAllEnabledAlarms() } returns enabledAlarmList
        }

        // Act & Assert
        mockkObject(BuildVersionUtil) {
            every { BuildVersionUtil.getCurrentSkdInt() } returns Build.VERSION_CODES.VANILLA_ICE_CREAM - 1

            val shouldPerformRecovery = ForceStopRecoveryHandler.shouldPerformForceStopRecoveryPreApi35(context, alarmRepository)
            assertFalse(shouldPerformRecovery)
        }
    }

    @Test
    fun shouldPerformForceStopRecoveryPreApi35_returnsFalse_nextAlarmFromThisApp_noEnabledAlarmsInDb() = runTest {
        // Arrange
        val myPackageName = "my.package"
        val pendingIntent = mockk<PendingIntent> {
            every { creatorPackage } returns myPackageName
        }
        val alarmClockInfo = mockk<AlarmManager.AlarmClockInfo> {
            every { showIntent } returns pendingIntent
        }
        val alarmManager = mockk<AlarmManager> {
            every { nextAlarmClock } returns alarmClockInfo
        }
        val context = mockk<Context> {
            every { getSystemService(AlarmManager::class.java) } returns alarmManager
            every { packageName } returns myPackageName
        }
        val alarmRepository = mockk<AlarmRepository> {
            coEvery { getAllEnabledAlarms() } returns emptyList()
        }

        // Act & Assert
        mockkObject(BuildVersionUtil) {
            every { BuildVersionUtil.getCurrentSkdInt() } returns Build.VERSION_CODES.VANILLA_ICE_CREAM - 1

            val shouldPerformRecovery = ForceStopRecoveryHandler.shouldPerformForceStopRecoveryPreApi35(context, alarmRepository)
            assertFalse(shouldPerformRecovery)
        }
    }

    // Device running API >= 35
    @Test
    fun shouldPerformForceStopRecoveryPreApi35_returnsTrue_ApiLevelAbove34() = runTest {
        // Arrange
        val context = mockk<Context>()
        val alarmRepository = mockk<AlarmRepository>()

        // Act & Assert
        mockkObject(BuildVersionUtil) {
            every { BuildVersionUtil.getCurrentSkdInt()} returns Build.VERSION_CODES.VANILLA_ICE_CREAM

            val shouldPerformRecovery = ForceStopRecoveryHandler.shouldPerformForceStopRecoveryPreApi35(context, alarmRepository)
            assertTrue(shouldPerformRecovery)
        }
    }

    /*
     * performForceStopRecoveryPreApi35
     */

    @Test
    fun performForceStopRecoveryPreApi35_sendsBroadcastWithProperArgs() {
        // Arrange
        val actionSlot = slot<String>()
        val context = mockk<Context> {
            every { sendBroadcast(any()) } just Runs
        }

        // Act & Assert
        mockkConstructor(Intent::class) {
            // This function is mocked only for Intents constructed with the specific parameters
            // that we expect in the production code. This is done so we can tell if the Intent
            // was constructed in the way we expect. If it were not constructed with AlarmRefreshReceiver::class.java
            // for example, then setAction() would not be mocked, and the test would fail.
            //
            // Furthermore, the return value here is just a generic mock.
            // Normally, this function would return "this", but there's no way to
            // directly access the mock created by mockkConstructor() here, but we
            // still have to return something. This is fine for this test because the
            // production code under test here does not use the return value of setAction().
            every {
                constructedWith<Intent>(
                    EqMatcher(context),
                    EqMatcher(AlarmRefreshReceiver::class.java)
                ).setAction(capture(actionSlot))
            } returns mockk<Intent>()

            ForceStopRecoveryHandler.performForceStopRecoveryPreApi35(context)
            assertEquals(AlarmRefreshReceiver.ACTION_FORCE_STOP_RECOVERY_PRE_API_35, actionSlot.captured)
        }
    }
}
