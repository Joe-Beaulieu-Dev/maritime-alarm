package com.octrobi.lavalarm.alarm.ui.alarmlist

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.alarm.data.model.Alarm
import com.octrobi.lavalarm.alarm.data.model.WeeklyRepeater
import com.octrobi.lavalarm.alarm.data.repository.AlarmRepository
import com.octrobi.lavalarm.core.extension.LocalDateTimeUtil
import com.octrobi.lavalarm.core.ui.notificationcheck.AppNotificationChannel
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme
import com.octrobi.lavalarm.testutil.NotificationChannelUtil
import com.octrobi.lavalarm.testutil.PermissionUtil
import io.mockk.every
import io.mockk.mockkConstructor
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class AlarmListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Alarm
    private val tueWedThu: Int = 28
    private val baseAlarmNonRepeating = Alarm(
        id = 1,
        name = "Non-Repeating Alarm",
        enabled = true,
        dateTime = LocalDateTimeUtil.nowTruncated().plusHours(1),
        weeklyRepeater = WeeklyRepeater(),
        ringtoneUri = "ringtoneUri",
        isVibrationEnabled = false,
        snoozeDateTime = null,
        snoozeDuration = 10
    )
    private val baseAlarmRepeating = baseAlarmNonRepeating.copy(
        id = 2,
        name = "Repeating Alarm",
        dateTime = baseAlarmNonRepeating.dateTime.plusHours(1),
        weeklyRepeater = WeeklyRepeater(tueWedThu)
    )
    private val alarmList = listOf(baseAlarmNonRepeating, baseAlarmRepeating)

    /*
     * AlarmListScreen - Alarm List
     */

    @Test
    fun alarmListScreen_DisplaysNoAlarmsCard_WhenNoAlarms_AndPermissionsAlreadyGranted() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val noAlarmsCardString = instrumentation.targetContext.getString(R.string.no_alarms)

        PermissionUtil.grantPermissionAuto(Manifest.permission.POST_NOTIFICATIONS)
        mockkConstructor(AlarmRepository::class) {
            every { anyConstructed<AlarmRepository>().getAllAlarmsFlow() } returns flowOf(emptyList())
            composeTestRule.setContent {
                LavalarmTheme {
                    AlarmListScreen(navigateToAlarmEditScreen = {})
                }
            }
        }

        composeTestRule.onNodeWithText(noAlarmsCardString).assertIsDisplayed()
    }

    @Test
    fun alarmListScreen_DisplaysAlarmCards_WhenThereAreAlarms_AndPermissionsAlreadyGranted() {
        PermissionUtil.grantPermissionAuto(Manifest.permission.POST_NOTIFICATIONS)
        mockkConstructor(AlarmRepository::class) {
            every { anyConstructed<AlarmRepository>().getAllAlarmsFlow() } returns flowOf(alarmList)
            composeTestRule.setContent {
                LavalarmTheme {
                    AlarmListScreen(navigateToAlarmEditScreen = {})
                }
            }
        }

        composeTestRule.onNodeWithText(baseAlarmNonRepeating.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(baseAlarmRepeating.name).assertIsDisplayed()
    }

    @Test
    fun alarmListScreen_DisplaysAlarmCards_WhenThereAreAlarms_AfterGrantingPermissionViaSystemDialog() {
        mockkConstructor(AlarmRepository::class) {
            every { anyConstructed<AlarmRepository>().getAllAlarmsFlow() } returns flowOf(alarmList)
            composeTestRule.setContent {
                LavalarmTheme {
                    AlarmListScreen(navigateToAlarmEditScreen = {})
                }
            }
            PermissionUtil.grantPermissionDialog()
        }

        composeTestRule.onNodeWithText(baseAlarmNonRepeating.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(baseAlarmRepeating.name).assertIsDisplayed()
    }

    /*
     * AlarmListScreen - Permission Gate
     */

    @Test
    fun alarmListScreen_ShowsPermissionGateScreen_WhenNotificationPermissionDenied() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val permissionGateString = context.getString(R.string.permission_required)

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmListScreen(navigateToAlarmEditScreen = {})
            }
        }
        PermissionUtil.denyPermissionDialog()

        composeTestRule.onNodeWithText(permissionGateString).assertIsDisplayed()
    }

    /*
     * AlarmListScreen - NotificationChannel Gate
     */

    @Test
    fun alarmListScreen_ShowsNotificationChannelGateScreen_WhenAlarmNotificationChannelIsDisabled() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val notificationGateString = context.getString(R.string.notification_required)

        NotificationChannelUtil.disableNotificationChannel(context, AppNotificationChannel.Alarm)
        PermissionUtil.grantPermissionAuto(Manifest.permission.POST_NOTIFICATIONS)
        composeTestRule.setContent {
            LavalarmTheme {
                AlarmListScreen(navigateToAlarmEditScreen = {})
            }
        }

        composeTestRule.onNodeWithText(notificationGateString).assertIsDisplayed()
    }
}
