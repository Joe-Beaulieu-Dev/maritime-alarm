package com.joebsource.lavalarm.core.ui.core

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.joebsource.lavalarm.R
import com.joebsource.lavalarm.alarm.data.model.Alarm
import com.joebsource.lavalarm.alarm.data.model.WeeklyRepeater
import com.joebsource.lavalarm.alarm.data.repository.AlarmRepository
import com.joebsource.lavalarm.core.extension.LocalDateTimeUtil
import com.joebsource.lavalarm.core.extension.toCountdownString
import com.joebsource.lavalarm.core.navigation.TopLevelNavHost
import com.joebsource.lavalarm.core.ui.theme.LavalarmTheme
import io.mockk.every
import io.mockk.mockkConstructor
import kotlinx.coroutines.flow.flowOf
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class CoreScreenTest {

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

    // Test Set Up
    companion object {

        @JvmStatic
        @BeforeClass
        fun setUp() {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            instrumentation.uiAutomation.grantRuntimePermission(
                instrumentation.targetContext.packageName,
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    /*
     * AlarmListScreen
     */

    @Test
    fun coreScreen_DisplaysExpectedUi_WhenOnAlarmListScreen_NoAlarms() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val nextAlarmCloudString = context.getString(R.string.no_active_alarms)
        val noAlarmsString = context.getString(R.string.no_alarms)
        val fabCreateAlarmContentDescription = context.getString(R.string.lava_fab_create_alarm_cd)
        val alarmListNavContentDescription = context.getString(R.string.nav_alarm_cd)
        val settingsNavContentDescription = context.getString(R.string.nav_settings_cd)

        composeTestRule.setContent {
            LavalarmTheme {
                TopLevelNavHost()
            }
        }

        composeTestRule.onNodeWithText(nextAlarmCloudString).assertIsDisplayed()
        composeTestRule.onNodeWithText(noAlarmsString).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(fabCreateAlarmContentDescription).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = alarmListNavContentDescription, useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = settingsNavContentDescription, useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun coreScreen_DisplaysExpectedUi_WhenOnAlarmListScreen_WithAlarms() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val nextAlarmCloudString = baseAlarmNonRepeating.toCountdownString(context)
        val fabCreateAlarmContentDescription = context.getString(R.string.lava_fab_create_alarm_cd)
        val alarmListNavContentDescription = context.getString(R.string.nav_alarm_cd)
        val settingsNavContentDescription = context.getString(R.string.nav_settings_cd)

        mockkConstructor(AlarmRepository::class) {
            every { anyConstructed<AlarmRepository>().getAllAlarmsFlow() } returns flowOf(alarmList)
            composeTestRule.setContent {
                LavalarmTheme {
                    TopLevelNavHost()
                }
            }
        }

        composeTestRule.onNodeWithText(nextAlarmCloudString).assertIsDisplayed()
        composeTestRule.onNodeWithText(baseAlarmNonRepeating.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(baseAlarmRepeating.name).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(fabCreateAlarmContentDescription).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = alarmListNavContentDescription, useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = settingsNavContentDescription, useUnmergedTree = true).assertIsDisplayed()
    }

    /*
     * SettingsScreen
     */

    @Test
    fun coreScreen_DisplaysExpectedUi_WhenOnSettingsScreen() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val nextAlarmCloudString = context.getString(R.string.no_active_alarms)
        val generalSettingsString = context.getString(R.string.settings_general)
        val alarmDefaultsString = context.getString(R.string.settings_alarm_defaults)
        val fabCreateAlarmContentDescription = context.getString(R.string.lava_fab_create_alarm_cd)
        val alarmListNavContentDescription = context.getString(R.string.nav_alarm_cd)
        val settingsNavContentDescription = context.getString(R.string.nav_settings_cd)

        composeTestRule.setContent {
            LavalarmTheme {
                TopLevelNavHost()
            }
        }

        // Navigate to SettingsScreen
        composeTestRule.onNodeWithContentDescription(label = settingsNavContentDescription, useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        // On SettingsScreen
        composeTestRule.onNodeWithText(nextAlarmCloudString).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(generalSettingsString).assertIsDisplayed()
        composeTestRule.onNodeWithText(alarmDefaultsString).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(fabCreateAlarmContentDescription).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescription(label = alarmListNavContentDescription, useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = settingsNavContentDescription, useUnmergedTree = true).assertIsDisplayed()
    }

    /*
     * Navigation Functionality
     */

    @Test
    fun coreScreen_ProperlyNavigates_WithBottomBarNavigation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val nextAlarmCloudString = context.getString(R.string.no_active_alarms)
        val noAlarmsString = context.getString(R.string.no_alarms)
        val generalSettingsString = context.getString(R.string.settings_general)
        val alarmDefaultsString = context.getString(R.string.settings_alarm_defaults)
        val fabCreateAlarmContentDescription = context.getString(R.string.lava_fab_create_alarm_cd)
        val alarmListNavContentDescription = context.getString(R.string.nav_alarm_cd)
        val settingsNavContentDescription = context.getString(R.string.nav_settings_cd)

        composeTestRule.setContent {
            LavalarmTheme {
                TopLevelNavHost()
            }
        }

        // On AlarmListScreen
        composeTestRule.onNodeWithText(nextAlarmCloudString).assertIsDisplayed()
        composeTestRule.onNodeWithText(noAlarmsString).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(fabCreateAlarmContentDescription).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = alarmListNavContentDescription, useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = settingsNavContentDescription, useUnmergedTree = true).assertIsDisplayed()

        // Navigate to SettingsScreen
        composeTestRule.onNodeWithContentDescription(label = settingsNavContentDescription, useUnmergedTree = true).performClick()

        // On SettingsScreen
        composeTestRule.onNodeWithText(nextAlarmCloudString).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(generalSettingsString).assertIsDisplayed()
        composeTestRule.onNodeWithText(alarmDefaultsString).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(fabCreateAlarmContentDescription).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescription(label = alarmListNavContentDescription, useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = settingsNavContentDescription, useUnmergedTree = true).assertIsDisplayed()

        // Navigate to AlarmListScreen
        composeTestRule.onNodeWithContentDescription(label = alarmListNavContentDescription, useUnmergedTree = true).performClick()

        // On AlarmListScreen
        composeTestRule.onNodeWithText(nextAlarmCloudString).assertIsDisplayed()
        composeTestRule.onNodeWithText(noAlarmsString).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(fabCreateAlarmContentDescription).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = alarmListNavContentDescription, useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = settingsNavContentDescription, useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun coreScreen_ProperlyNavigates_FromAlarmListScreen_ToAlarmCreationScreen() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val nextAlarmCloudString = context.getString(R.string.no_active_alarms)
        val noAlarmsString = context.getString(R.string.no_alarms)
        val fabCreateAlarmContentDescription = context.getString(R.string.lava_fab_create_alarm_cd)
        val alarmListNavContentDescription = context.getString(R.string.nav_alarm_cd)
        val settingsNavContentDescription = context.getString(R.string.nav_settings_cd)
        val alarmCreationScreenTitle = context.getString(R.string.alarm_creation_screen_title)

        composeTestRule.setContent {
            LavalarmTheme {
                TopLevelNavHost()
            }
        }

        // On AlarmListScreen
        composeTestRule.onNodeWithText(nextAlarmCloudString).assertIsDisplayed()
        composeTestRule.onNodeWithText(noAlarmsString).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(fabCreateAlarmContentDescription).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = alarmListNavContentDescription, useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = settingsNavContentDescription, useUnmergedTree = true).assertIsDisplayed()

        // Navigate to AlarmCreationScreen
        composeTestRule.onNodeWithContentDescription(fabCreateAlarmContentDescription).performClick()

        // On AlarmCreationScreen
        composeTestRule.onNodeWithText(alarmCreationScreenTitle).assertIsDisplayed()
    }

    @Test
    fun coreScreen_ProperlyNavigates_FromAlarmListScreen_ToAlarmEditScreen() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val nextAlarmCloudString = baseAlarmNonRepeating.toCountdownString(context)
        val fabCreateAlarmContentDescription = context.getString(R.string.lava_fab_create_alarm_cd)
        val alarmListNavContentDescription = context.getString(R.string.nav_alarm_cd)
        val settingsNavContentDescription = context.getString(R.string.nav_settings_cd)
        val alarmEditScreenTitle = context.getString(R.string.alarm_edit_screen_title)

        mockkConstructor(AlarmRepository::class) {
            every { anyConstructed<AlarmRepository>().getAllAlarmsFlow() } returns flowOf(alarmList)
            every { anyConstructed<AlarmRepository>().getAlarmFlow(baseAlarmNonRepeating.id) } returns flowOf(baseAlarmNonRepeating)
            composeTestRule.setContent {
                LavalarmTheme {
                    TopLevelNavHost()
                }
            }

            // On AlarmListScreen
            composeTestRule.onNodeWithText(nextAlarmCloudString).assertIsDisplayed()
            composeTestRule.onNodeWithText(baseAlarmNonRepeating.name).assertIsDisplayed()
            composeTestRule.onNodeWithText(baseAlarmRepeating.name).assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription(fabCreateAlarmContentDescription).assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription(label = alarmListNavContentDescription, useUnmergedTree = true).assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription(label = settingsNavContentDescription, useUnmergedTree = true).assertIsDisplayed()

            // Navigate to AlarmEditScreen
            composeTestRule.onNodeWithText(baseAlarmNonRepeating.name).performClick()

            // On AlarmEditScreen
            composeTestRule.onNodeWithText(alarmEditScreenTitle).assertIsDisplayed()
            composeTestRule.onNodeWithText(baseAlarmNonRepeating.name).assertIsDisplayed()
            composeTestRule.onNodeWithText(baseAlarmRepeating.name).assertIsNotDisplayed()
        }
    }
}
