package com.example.alarmscratch.core.ui.core.component

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
import com.example.alarmscratch.R
import com.example.alarmscratch.core.navigation.Destination
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
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
    fun nextAlarmCloudContent_DisplaysAlarmCountdownText_WhenOnAlarmListScreen_AndHasActiveAlarm() {
        val countdownText = "countdownText"
        val alarmCountdownState = AlarmCountdownState.Success(Icons.Default.Alarm, countdownText)

        composeTestRule.setContent {
            AlarmScratchTheme {
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
    fun nextAlarmCloudContent_DisplaysNoActiveAlarmsText_WhenOnAlarmListScreen_AndHasNoActiveAlarms() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val countdownText = context.getString(R.string.no_active_alarms)
        val alarmCountdownState = AlarmCountdownState.Success(Icons.Default.AlarmOff, countdownText)

        composeTestRule.setContent {
            AlarmScratchTheme {
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
    fun nextAlarmCloudContent_DisplaysNothing_WhenNotOnAlarmListScreen() {
        val countdownText = "countdownText"
        val alarmCountdownState = AlarmCountdownState.Success(Icons.Default.Alarm, countdownText)

        composeTestRule.setContent {
            AlarmScratchTheme {
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
