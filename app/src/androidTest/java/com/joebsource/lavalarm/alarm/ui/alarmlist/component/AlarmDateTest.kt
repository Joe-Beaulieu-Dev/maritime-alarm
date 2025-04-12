package com.joebsource.lavalarm.alarm.ui.alarmlist.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.joebsource.lavalarm.R
import com.joebsource.lavalarm.alarm.data.model.Alarm
import com.joebsource.lavalarm.alarm.data.model.WeeklyRepeater
import com.joebsource.lavalarm.core.extension.LocalDateTimeUtil
import com.joebsource.lavalarm.core.ui.theme.LavalarmTheme
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class AlarmDateTest {

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

    /*
     * AlarmDate
     */

    @Test
    fun alarmDate_ShowsGeneralWeekText_WhenAlarmIsRepeating_AndAlarmIsEnabled() {
        val alarm = baseAlarmNonRepeating.copy(weeklyRepeater = WeeklyRepeater(tueWedThu))

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmDate(alarm = alarm)
            }
        }

        composeTestRule.onNodeWithText("S M T W T F S").assertIsDisplayed()
    }

    @Test
    fun alarmDate_ShowsGeneralWeekText_WhenAlarmIsRepeating_AndAlarmIsDisabled() {
        val alarm = baseAlarmNonRepeating.copy(enabled = false, weeklyRepeater = WeeklyRepeater(tueWedThu))

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmDate(alarm = alarm)
            }
        }

        composeTestRule.onNodeWithText("S M T W T F S").assertIsDisplayed()
    }

    @Test
    fun alarmDate_DisplaysTextToday_WhenAlarmIsNotRepeating_AndAlarmIsForToday() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val now = LocalDateTimeUtil.nowTruncated()
        val alarm = baseAlarmNonRepeating.copy(dateTime = now)

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            composeTestRule.setContent {
                LavalarmTheme {
                    AlarmDate(alarm = alarm)
                }
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.date_today)).assertIsDisplayed()
    }

    @Test
    fun alarmDate_DisplaysTextTomorrow_WhenAlarmIsNotRepeating_AndAlarmIsForTomorrow() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val now = LocalDateTimeUtil.nowTruncated()
        val alarm = baseAlarmNonRepeating.copy(dateTime = now)

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now.minusDays(1)
            composeTestRule.setContent {
                LavalarmTheme {
                    AlarmDate(alarm = alarm)
                }
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.date_tomorrow)).assertIsDisplayed()
    }

    @Test
    fun alarmDate_DisplaysTextCalendarDate_WhenAlarmIsNotRepeating_AndAlarmIsBeyondTomorrow() {
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 28),
            LocalTime.NOON
        )
        val alarm = baseAlarmNonRepeating.copy(dateTime = dateTime)
        val expectedString = "Fri, Mar 28th 2025"

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns dateTime.minusDays(2)
            composeTestRule.setContent {
                LavalarmTheme {
                    AlarmDate(alarm = alarm)
                }
            }
        }

        composeTestRule.onNodeWithText(expectedString).assertIsDisplayed()
    }

    // The conditions for this test should not happen naturally, but it should
    // still be tested to ensure that it does not cause problems with the composable.
    @Test
    fun alarmDate_DisplaysTextCalendarDate_WhenAlarmIsNotRepeating_AndAlarmIsBeforeToday() {
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 28),
            LocalTime.NOON
        )
        val alarm = baseAlarmNonRepeating.copy(dateTime = dateTime)
        val expectedString = "Fri, Mar 28th 2025"

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns dateTime.plusDays(1)
            composeTestRule.setContent {
                LavalarmTheme {
                    AlarmDate(alarm = alarm)
                }
            }
        }

        composeTestRule.onNodeWithText(expectedString).assertIsDisplayed()
    }

    @Test
    fun alarmDate_DisplaysTextNotScheduled_WhenAlarmIsNotRepeating_AndAlarmIsDisabled() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val alarm = baseAlarmNonRepeating.copy(enabled = false)
        val expectedString = context.getString(R.string.not_scheduled)

        composeTestRule.setContent {
            LavalarmTheme {
                AlarmDate(alarm = alarm)
            }
        }

        composeTestRule.onNodeWithText(expectedString).assertIsDisplayed()
    }
}
