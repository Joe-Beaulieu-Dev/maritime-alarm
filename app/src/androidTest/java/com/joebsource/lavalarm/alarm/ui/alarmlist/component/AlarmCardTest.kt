package com.joebsource.lavalarm.alarm.ui.alarmlist.component

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.joebsource.lavalarm.R
import com.joebsource.lavalarm.alarm.data.model.Alarm
import com.joebsource.lavalarm.alarm.data.model.WeeklyRepeater
import com.joebsource.lavalarm.core.extension.LocalDateTimeUtil
import com.joebsource.lavalarm.core.extension.to12HourNotificationDateTimeString
import com.joebsource.lavalarm.core.extension.to24HourNotificationDateTimeString
import com.joebsource.lavalarm.core.ui.theme.LavalarmTheme
import com.joebsource.lavalarm.settings.data.model.TimeDisplay
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class AlarmCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Alarm
    private val tueWedThu: Int = 28
    private val baseAlarmNonRepeating = Alarm(
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
    private val baseAlarmRepeating = baseAlarmNonRepeating.copy(weeklyRepeater = WeeklyRepeater(tueWedThu))

    /*
     *************
     * AlarmCard *
     *************
     */

    /*
     * AlarmCard Static Display - NonRepeating Alarm
     */

    @Test
    fun alarmCard_DisplaysAlarmData_ForEnabledAlarm_NonRepeating_NonSnoozed_12HourDisplay() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30))
        val alarm = baseAlarmNonRepeating.copy(dateTime = dateTime)
        val expectedTime = "${dateTime.toLocalTime().hour}:${dateTime.toLocalTime().minute}"
        val expectedAmPm = context.getString(R.string.time_am)
        val expectedDate = context.getString(R.string.date_today)

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmCard(
                    alarm = alarm,
                    timeDisplay = TimeDisplay.TwelveHour,
                    onAlarmToggled = { _, _ -> },
                    onAlarmDeleted = { _, _ -> },
                    navigateToAlarmEditScreen = {}
                )
            }
        }

        composeTestRule.onNodeWithText(alarm.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedTime).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedAmPm).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedDate).assertIsDisplayed()
        composeTestRule.onNode(isToggleable()).assertIsDisplayed().assertIsOn()
    }

    @Test
    fun alarmCard_DisplaysAlarmData_ForEnabledAlarm_NonRepeating_Snoozed_12HourDisplay() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 30))
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = dateTime,
            snoozeDateTime = dateTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong())
        )
        val expectedTime = "${dateTime.toLocalTime().hour - 12}:${dateTime.toLocalTime().minute}"
        val expectedAmPm = context.getString(R.string.time_pm)
        val expectedDate = context.getString(R.string.date_today)
        val expectedSnoozeString = "${context.getString(R.string.snooze_indicator)} " +
                alarm.snoozeDateTime!!.to12HourNotificationDateTimeString(context)

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmCard(
                    alarm = alarm,
                    timeDisplay = TimeDisplay.TwelveHour,
                    onAlarmToggled = { _, _ -> },
                    onAlarmDeleted = { _, _ -> },
                    navigateToAlarmEditScreen = {}
                )
            }
        }

        composeTestRule.onNodeWithText(alarm.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedTime).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedAmPm).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedDate).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedSnoozeString).assertIsDisplayed()
        composeTestRule.onNode(isToggleable()).assertIsDisplayed().assertIsOn()
    }

    @Test
    fun alarmCard_DisplaysAlarmData_ForEnabledAlarm_NonRepeating_Snoozed_24HourDisplay() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30))
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = dateTime,
            snoozeDateTime = dateTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong())
        )
        val expectedTime = "0${dateTime.toLocalTime().hour}:${dateTime.toLocalTime().minute}"
        val amPm = context.getString(R.string.time_am)
        val expectedDate = context.getString(R.string.date_today)
        val expectedSnoozeString = "${context.getString(R.string.snooze_indicator)} " +
                alarm.snoozeDateTime!!.to24HourNotificationDateTimeString()

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmCard(
                    alarm = alarm,
                    timeDisplay = TimeDisplay.TwentyFourHour,
                    onAlarmToggled = { _, _ -> },
                    onAlarmDeleted = { _, _ -> },
                    navigateToAlarmEditScreen = {}
                )
            }
        }

        composeTestRule.onNodeWithText(alarm.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedTime).assertIsDisplayed()
        composeTestRule.onNodeWithText(amPm).assertDoesNotExist()
        composeTestRule.onNodeWithText(expectedDate).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedSnoozeString).assertIsDisplayed()
        composeTestRule.onNode(isToggleable()).assertIsDisplayed().assertIsOn()
    }

    @Test
    fun alarmCard_DisplaysAlarmData_ForDisabledAlarm_NonRepeating_NonSnoozed_12HourDisplay() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30))
        val alarm = baseAlarmNonRepeating.copy(enabled = false, dateTime = dateTime)
        val expectedTime = "${dateTime.toLocalTime().hour}:${dateTime.toLocalTime().minute}"
        val expectedAmPm = context.getString(R.string.time_am)
        val expectedDate = context.getString(R.string.not_scheduled)

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmCard(
                    alarm = alarm,
                    timeDisplay = TimeDisplay.TwelveHour,
                    onAlarmToggled = { _, _ -> },
                    onAlarmDeleted = { _, _ -> },
                    navigateToAlarmEditScreen = {}
                )
            }
        }

        composeTestRule.onNodeWithText(alarm.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedTime).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedAmPm).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedDate).assertIsDisplayed()
        composeTestRule.onNode(isToggleable()).assertIsDisplayed().assertIsOff()
    }

    // The conditions for this test should not occur naturally since disabled Alarms should never have snooze data.
    // However, we should still test to make sure that Alarms display their snooze data as long as it exists.
    @Test
    fun alarmCard_DisplaysAlarmData_ForDisabledAlarm_NonRepeating_Snoozed_12HourDisplay() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30))
        val alarm = baseAlarmNonRepeating.copy(
            enabled = false,
            dateTime = dateTime,
            snoozeDateTime = dateTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong())
        )
        val expectedTime = "${dateTime.toLocalTime().hour}:${dateTime.toLocalTime().minute}"
        val expectedAmPm = context.getString(R.string.time_am)
        val expectedDate = context.getString(R.string.not_scheduled)
        val expectedSnoozeString = "${context.getString(R.string.snooze_indicator)} " +
                alarm.snoozeDateTime!!.to12HourNotificationDateTimeString(context)

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmCard(
                    alarm = alarm,
                    timeDisplay = TimeDisplay.TwelveHour,
                    onAlarmToggled = { _, _ -> },
                    onAlarmDeleted = { _, _ -> },
                    navigateToAlarmEditScreen = {}
                )
            }
        }

        composeTestRule.onNodeWithText(alarm.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedTime).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedAmPm).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedDate).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedSnoozeString).assertIsDisplayed()
        composeTestRule.onNode(isToggleable()).assertIsDisplayed().assertIsOff()
    }

    @Test
    fun alarmCard_StillDisplaysAlarmNameText_WhenAlarmNameIsEmptyString() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val expectedName = ""
        val dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30))
        val alarm = baseAlarmNonRepeating.copy(name = expectedName, dateTime = dateTime)
        val expectedTime = "${dateTime.toLocalTime().hour}:${dateTime.toLocalTime().minute}"
        val expectedAmPm = context.getString(R.string.time_am)
        val expectedDate = context.getString(R.string.date_today)

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmCard(
                    alarm = alarm,
                    timeDisplay = TimeDisplay.TwelveHour,
                    onAlarmToggled = { _, _ -> },
                    onAlarmDeleted = { _, _ -> },
                    navigateToAlarmEditScreen = {}
                )
            }
        }

        composeTestRule.onNodeWithText(alarm.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedTime).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedAmPm).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedDate).assertIsDisplayed()
        composeTestRule.onNode(isToggleable()).assertIsDisplayed().assertIsOn()
    }

    /*
     * AlarmCard Static Display - Repeating Alarm
     */

    @Test
    fun alarmCard_DisplaysAlarmData_ForEnabledAlarm_Repeating_NonSnoozed_12HourDisplay() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30))
        val alarm = baseAlarmRepeating.copy(dateTime = dateTime)
        val expectedTime = "${dateTime.toLocalTime().hour}:${dateTime.toLocalTime().minute}"
        val expectedAmPm = context.getString(R.string.time_am)
        val expectedDate = "S M T W T F S"

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmCard(
                    alarm = alarm,
                    timeDisplay = TimeDisplay.TwelveHour,
                    onAlarmToggled = { _, _ -> },
                    onAlarmDeleted = { _, _ -> },
                    navigateToAlarmEditScreen = {}
                )
            }
        }

        composeTestRule.onNodeWithText(alarm.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedTime).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedAmPm).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedDate).assertIsDisplayed()
        composeTestRule.onNode(isToggleable()).assertIsDisplayed().assertIsOn()
    }

    @Test
    fun alarmCard_DisplaysAlarmData_ForDisabledAlarm_Repeating_NonSnoozed_12HourDisplay() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 30))
        val alarm = baseAlarmRepeating.copy(enabled = false, dateTime = dateTime)
        val expectedTime = "${dateTime.toLocalTime().hour}:${dateTime.toLocalTime().minute}"
        val expectedAmPm = context.getString(R.string.time_am)
        val expectedDate = "S M T W T F S"

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmCard(
                    alarm = alarm,
                    timeDisplay = TimeDisplay.TwelveHour,
                    onAlarmToggled = { _, _ -> },
                    onAlarmDeleted = { _, _ -> },
                    navigateToAlarmEditScreen = {}
                )
            }
        }

        composeTestRule.onNodeWithText(alarm.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedTime).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedAmPm).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedDate).assertIsDisplayed()
        composeTestRule.onNode(isToggleable()).assertIsDisplayed().assertIsOff()
    }

    /*
     * Switch Functionality
     */

    @Test
    fun alarmCard_AlarmToggleSwitch_PerformsClickAction() {
        val alarm = baseAlarmNonRepeating
        var switchPressed = false

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmCard(
                    alarm = alarm,
                    timeDisplay = TimeDisplay.TwelveHour,
                    onAlarmToggled = { _, _ -> switchPressed = true },
                    onAlarmDeleted = { _, _ -> },
                    navigateToAlarmEditScreen = {}
                )
            }
        }

        composeTestRule.onNode(isToggleable()).assertIsDisplayed().performClick()
        assertTrue(switchPressed)
    }

    /*
     * Dropdown Functionality
     */

    @Test
    fun alarmCard_DropdownMenu_ProperlyDropsDown_AndPerformsClickAction() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val alarm = baseAlarmRepeating
        val expectedMenuText = context.getString(R.string.menu_delete)
        var deletePressed = false

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmCard(
                    alarm = alarm,
                    timeDisplay = TimeDisplay.TwelveHour,
                    onAlarmToggled = { _, _ -> },
                    onAlarmDeleted = { _, _ -> deletePressed = true },
                    navigateToAlarmEditScreen = {}
                )
            }
        }

        composeTestRule
            .onNode(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assertIsDisplayed()
            .performClick()
        composeTestRule.onNodeWithText(expectedMenuText).assertIsDisplayed().performClick()
        assertTrue(deletePressed)
    }

    /*
     * Navigation Functionality
     */

    @Test
    fun alarmCard_PerformsNavigationAction_WhenAlarmCardIsClicked() {
        val alarm = baseAlarmNonRepeating
        var alarmCardClicked = false

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmCard(
                    alarm = alarm,
                    timeDisplay = TimeDisplay.TwelveHour,
                    onAlarmToggled = { _, _ -> },
                    onAlarmDeleted = { _, _ -> },
                    navigateToAlarmEditScreen = { alarmCardClicked = true }
                )
            }
        }

        composeTestRule.onRoot().performClick()
        assertTrue(alarmCardClicked)
    }

    /*
     ****************
     * NoAlarmsCard *
     ****************
     */

    @Test
    fun noAlarmsCard_DisplaysProperText() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val expectedString = context.getString(R.string.no_alarms)

        composeTestRule.setContent {
            LavalarmTheme {
                NoAlarmsCard()
            }
        }

        composeTestRule.onNodeWithText(expectedString).assertIsDisplayed()
    }
}
