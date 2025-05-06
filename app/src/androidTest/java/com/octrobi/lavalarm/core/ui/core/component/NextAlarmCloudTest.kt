package com.octrobi.lavalarm.core.ui.core.component

import android.Manifest
import android.content.BroadcastReceiver
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.core.navigation.Destination
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme
import com.octrobi.lavalarm.testutil.PermissionUtil
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class NextAlarmCloudTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /*
     * NextAlarmCloudContent
     */

    @Test
    fun nextAlarmCloudContent_DisplaysAlarmCountdownText_WhenOnAlarmListScreen_AndHasActiveAlarm_AndPermissionsAlreadyGranted() {
        val countdownText = "countdownText"
        val alarmCountdownState = AlarmCountdownState.Success(Icons.Default.Alarm, countdownText)

        PermissionUtil.grantPermissionAuto(Manifest.permission.POST_NOTIFICATIONS)
        composeTestRule.setContent {
            LavalarmTheme {
                NextAlarmCloudContent(
                    currentCoreDestination = Destination.AlarmListScreen,
                    alarmCountdownState = alarmCountdownState,
                    visibleState = MutableTransitionState(initialState = true),
                    timeChangeReceiver = mockk<BroadcastReceiver>(relaxed = true)
                )
            }
        }

        composeTestRule.onNodeWithText(countdownText).assertIsDisplayed()
    }

    @Test
    fun nextAlarmCloudContent_DisplaysNoActiveAlarmsText_WhenOnAlarmListScreen_AndHasNoActiveAlarms_AndPermissionsAlreadyGranted() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val countdownText = context.getString(R.string.no_active_alarms)
        val alarmCountdownState = AlarmCountdownState.Success(Icons.Default.AlarmOff, countdownText)

        PermissionUtil.grantPermissionAuto(Manifest.permission.POST_NOTIFICATIONS)
        composeTestRule.setContent {
            LavalarmTheme {
                NextAlarmCloudContent(
                    currentCoreDestination = Destination.AlarmListScreen,
                    alarmCountdownState = alarmCountdownState,
                    visibleState = MutableTransitionState(initialState = true),
                    timeChangeReceiver = mockk<BroadcastReceiver>(relaxed = true)
                )
            }
        }

        composeTestRule.onNodeWithText(countdownText).assertIsDisplayed()
    }

    @Test
    fun nextAlarmCloudContent_DisplaysNothing_WhenOnAlarmListScreen_AndHasActiveAlarm_WhenNotificationPermissionDenied() {
        val countdownText = "countdownText"
        val alarmCountdownState = AlarmCountdownState.Success(Icons.Default.Alarm, countdownText)

        composeTestRule.setContent {
            LavalarmTheme {
                NextAlarmCloudContent(
                    currentCoreDestination = Destination.AlarmListScreen,
                    alarmCountdownState = alarmCountdownState,
                    visibleState = MutableTransitionState(initialState = true),
                    timeChangeReceiver = mockk<BroadcastReceiver>(relaxed = true)
                )
            }
        }

        composeTestRule.onNodeWithText(countdownText).assertIsNotDisplayed()
    }

    @Test
    fun nextAlarmCloudContent_DisplaysNothing_WhenNotOnAlarmListScreen_AndPermissionsAlreadyGranted() {
        val countdownText = "countdownText"
        val alarmCountdownState = AlarmCountdownState.Success(Icons.Default.Alarm, countdownText)

        PermissionUtil.grantPermissionAuto(Manifest.permission.POST_NOTIFICATIONS)
        composeTestRule.setContent {
            LavalarmTheme {
                NextAlarmCloudContent(
                    currentCoreDestination = Destination.SettingsScreen,
                    alarmCountdownState = alarmCountdownState,
                    visibleState = MutableTransitionState(initialState = false),
                    timeChangeReceiver = mockk<BroadcastReceiver>(relaxed = true)
                )
            }
        }

        composeTestRule.onNodeWithText(countdownText).assertIsNotDisplayed()
    }
}
