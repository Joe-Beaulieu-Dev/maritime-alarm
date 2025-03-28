package com.example.alarmscratch.alarm.ui.alarmlist.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class AlarmDateTest {

    @get: Rule
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
            AlarmScratchTheme {
                AlarmDate(alarm = alarm)
            }
        }

        composeTestRule.onNode(hasText("S M T W T F S")).assertIsDisplayed()
    }

    @Test
    fun alarmDate_ShowsGeneralWeekText_WhenAlarmIsRepeating_AndAlarmIsDisabled() {
        val alarm = baseAlarmNonRepeating.copy(enabled = false, weeklyRepeater = WeeklyRepeater(tueWedThu))

        composeTestRule.setContent {
            AlarmScratchTheme {
                AlarmDate(alarm = alarm)
            }
        }

        composeTestRule.onNode(hasText("S M T W T F S")).assertIsDisplayed()
    }

    @Test
    fun alarmDate_DisplaysTextToday_WhenAlarmIsNotRepeating_AndAlarmIsForToday() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val now = LocalDateTimeUtil.nowTruncated()
        val alarm = baseAlarmNonRepeating.copy(dateTime = now)

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            composeTestRule.setContent {
                AlarmScratchTheme {
                    AlarmDate(alarm = alarm)
                }
            }
        }

        composeTestRule.onNode(hasText(context.getString(R.string.date_today))).assertIsDisplayed()
    }

    @Test
    fun alarmDate_DisplaysTextTomorrow_WhenAlarmIsNotRepeating_AndAlarmIsForTomorrow() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val now = LocalDateTimeUtil.nowTruncated()
        val alarm = baseAlarmNonRepeating.copy(dateTime = now)

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now.minusDays(1)
            composeTestRule.setContent {
                AlarmScratchTheme {
                    AlarmDate(alarm = alarm)
                }
            }
        }

        composeTestRule.onNode(hasText(context.getString(R.string.date_tomorrow))).assertIsDisplayed()
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
                AlarmScratchTheme {
                    AlarmDate(alarm = alarm)
                }
            }
        }

        composeTestRule.onNode(hasText(expectedString)).assertIsDisplayed()
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
                AlarmScratchTheme {
                    AlarmDate(alarm = alarm)
                }
            }
        }

        composeTestRule.onNode(hasText(expectedString)).assertIsDisplayed()
    }

    @Test
    fun alarmDate_DisplaysTextNotScheduled_WhenAlarmIsNotRepeating_AndAlarmIsDisabled() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val alarm = baseAlarmNonRepeating.copy(enabled = false)
        val expectedString = context.getString(R.string.not_scheduled)

        composeTestRule.setContent {
            AlarmScratchTheme {
                AlarmDate(alarm = alarm)
            }
        }

        composeTestRule.onNode(hasText(expectedString)).assertIsDisplayed()
    }
}
