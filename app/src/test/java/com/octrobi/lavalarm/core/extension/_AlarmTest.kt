package com.octrobi.lavalarm.core.extension

import android.content.Context
import android.media.Ringtone
import com.octrobi.lavalarm.alarm.data.model.Alarm
import com.octrobi.lavalarm.alarm.data.model.AlarmExecutionData
import com.octrobi.lavalarm.alarm.data.model.AlarmStringFormatter
import com.octrobi.lavalarm.alarm.data.model.WeeklyRepeater
import com.octrobi.lavalarm.alarm.util.AlarmUtil
import com.octrobi.lavalarm.core.data.repository.RingtoneRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

@Suppress("ClassName")
class _AlarmTest {

    // General
    private val now = LocalDateTimeUtil.nowTruncated()

    // Alarm
    private val baseAlarmNonRepeating = Alarm(
        id = 1,
        name = "name",
        enabled = true,
        dateTime = now,
        weeklyRepeater = WeeklyRepeater(),
        ringtoneUri = "ringtoneUri",
        isVibrationEnabled = false,
        snoozeDateTime = null,
        snoozeDuration = 10
    )
    private val arbitraryWeeklyRepeater = WeeklyRepeater()
        .withDay(WeeklyRepeater.Day.WEDNESDAY)
        .withDay(WeeklyRepeater.Day.THURSDAY)

    /*
     * toAlarmExecutionData
     */

    @Test
    fun toAlarmExecutionData_ProperlyCreates_AlarmExecutionData_WhenAlarmIsNotSnoozed() {
        val alarm = baseAlarmNonRepeating
        val expectedAlarmExecutionData = AlarmExecutionData(
            id = alarm.id,
            name = alarm.name,
            executionDateTime = alarm.dateTime,
            encodedRepeatingDays = alarm.weeklyRepeater.toEncodedRepeatingDays(),
            ringtoneUri = alarm.ringtoneUri,
            isVibrationEnabled = alarm.isVibrationEnabled,
            snoozeDuration = alarm.snoozeDuration
        )

        val actualAlarmExecutionData = alarm.toAlarmExecutionData()

        assertEquals(expectedAlarmExecutionData, actualAlarmExecutionData)
    }

    @Test
    fun toAlarmExecutionData_ProperlyCreates_AlarmExecutionData_WhenAlarmIsSnoozed() {
        val alarm = baseAlarmNonRepeating.copy(
            snoozeDateTime = baseAlarmNonRepeating.dateTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong())
        )
        val expectedAlarmExecutionData = AlarmExecutionData(
            id = alarm.id,
            name = alarm.name,
            executionDateTime = alarm.snoozeDateTime!!,
            encodedRepeatingDays = alarm.weeklyRepeater.toEncodedRepeatingDays(),
            ringtoneUri = alarm.ringtoneUri,
            isVibrationEnabled = alarm.isVibrationEnabled,
            snoozeDuration = alarm.snoozeDuration
        )

        val actualAlarmExecutionData = alarm.toAlarmExecutionData()

        assertEquals(expectedAlarmExecutionData, actualAlarmExecutionData)
    }

    /*
     * withFuturizedDateTime
     */

    @Test
    fun withFuturizedDateTime_ReturnsNextRepeatingDateTime_RepeatingAlarm() {
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = now.minusHours(1),
            weeklyRepeater = arbitraryWeeklyRepeater
        )
        val expectedDateTime = alarm.dateTime.plusDays(1)
        val expectedAlarm = alarm.copy(dateTime = expectedDateTime)

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns expectedDateTime

                assertEquals(expectedAlarm, alarm.withFuturizedDateTime())
                verify { AlarmUtil.nextRepeatingDateTime(alarm.dateTime, alarm.weeklyRepeater) }
            }
        }
    }

    @Test
    fun withFuturizedDateTime_ReturnsAlarmTimeForTomorrow_NonRepeatingAlarm_IsInPast_AndSettingToToday_WouldBeInPast() {
        val alarm = baseAlarmNonRepeating.copy(dateTime = now.minusDays(1).minusHours(1))
        val expectedAlarm = alarm.copy(
            dateTime = LocalDateTime.of(now.toLocalDate().plusDays(1), alarm.dateTime.toLocalTime())
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            assertEquals(expectedAlarm, alarm.withFuturizedDateTime())
        }
    }

    @Test
    fun withFuturizedDateTime_ReturnsAlarmTimeForTomorrow_NonRepeatingAlarm_IsInPast_AndSettingToToday_WouldBeNow() {
        val alarm = baseAlarmNonRepeating.copy(dateTime = now.minusDays(1))
        val expectedAlarm = alarm.copy(
            dateTime = LocalDateTime.of(now.toLocalDate().plusDays(1), alarm.dateTime.toLocalTime())
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            assertEquals(expectedAlarm, alarm.withFuturizedDateTime())
        }
    }

    @Test
    fun withFuturizedDateTime_ReturnsAlarmTimeForToday_NonRepeatingAlarm_IsInPast_AndSettingToToday_WouldBeInFuture() {
        val alarm = baseAlarmNonRepeating.copy(dateTime = now.minusDays(1).plusHours(1))
        val expectedAlarm = alarm.copy(
            dateTime = LocalDateTime.of(now.toLocalDate(), alarm.dateTime.toLocalTime())
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            assertEquals(expectedAlarm, alarm.withFuturizedDateTime())
        }
    }

    @Test
    fun withFuturizedDateTime_ReturnsAlarmTimeForTomorrow_NonRepeatingAlarm_IsNow() {
        val alarm = baseAlarmNonRepeating
        val expectedAlarm = alarm.copy(dateTime = alarm.dateTime.plusDays(1))

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            assertEquals(expectedAlarm, alarm.withFuturizedDateTime())
        }
    }

    @Test
    fun withFuturizedDateTime_ReturnsSameDateTime_NonRepeatingAlarm_IsInFuture() {
        val alarm = baseAlarmNonRepeating.copy(dateTime = now.plusHours(1))

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            assertEquals(alarm, alarm.withFuturizedDateTime())
        }
    }

    /*
     * isRepeating
     */

    @Test
    fun isRepeating_ReturnsTrue_WhenAlarmIsRepeating() {
        val alarm = baseAlarmNonRepeating.copy(weeklyRepeater = arbitraryWeeklyRepeater)
        assertTrue(alarm.isRepeating())
    }

    @Test
    fun isRepeating_ReturnsFalse_WhenAlarmIsNotRepeating() {
        assertFalse(baseAlarmNonRepeating.isRepeating())
    }

    /*
     * isSnoozed
     */

    @Test
    fun isSnoozed_ReturnsTrue_WhenAlarmIsSnoozed() {
        val alarm = baseAlarmNonRepeating.copy(
            snoozeDateTime = baseAlarmNonRepeating.dateTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong())
        )
        assertTrue(alarm.isSnoozed())
    }

    @Test
    fun isSnoozed_ReturnsFalse_WhenAlarmIsNotSnoozed() {
        assertFalse(baseAlarmNonRepeating.isSnoozed())
    }

    /*
     * isDirty - Repeating Alarm
     */

    // Snoozed
    @Test
    fun isDirty_ReturnsTrue_RepeatingAlarm_IsEnabled_AndSnoozed_AndInPast() {
        val alarmTime = now.minusHours(1)
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = alarmTime,
            snoozeDateTime = alarmTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong()),
            weeklyRepeater = arbitraryWeeklyRepeater
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns alarm.dateTime
                assertTrue(alarm.isDirty())
            }
        }
    }

    @Test
    fun isDirty_ReturnsTrue_RepeatingAlarm_IsEnabled_AndSnoozed_AndIsNow() {
        val alarmTime = now.minusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong())
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = alarmTime,
            snoozeDateTime = alarmTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong()),
            weeklyRepeater = arbitraryWeeklyRepeater
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns alarm.dateTime
                assertTrue(alarm.isDirty())
            }
        }
    }

    @Test
    fun isDirty_ReturnsTrue_RepeatingAlarm_IsEnabled_AndSnoozed_AndInFuture_AndIsBeforeNextRepeating() {
        val alarmTime = now.plusHours(1)
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = alarmTime,
            snoozeDateTime = alarmTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong()),
            weeklyRepeater = arbitraryWeeklyRepeater
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns alarm.dateTime.plusDays(1)
                assertTrue(alarm.isDirty())
            }
        }
    }

    @Test
    fun isDirty_ReturnsTrue_RepeatingAlarm_IsEnabled_AndSnoozed_AndInFuture_AndAfterNextRepeating() {
        val alarmTime = now.plusDays(1).plusHours(1)
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = alarmTime,
            snoozeDateTime = alarmTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong()),
            weeklyRepeater = arbitraryWeeklyRepeater
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns alarm.dateTime.minusDays(1)
                assertTrue(alarm.isDirty())
            }
        }
    }

    @Test
    fun isDirty_ReturnsFalse_RepeatingAlarm_IsDisabled_AndSnoozed() {
        val alarmTime = now.plusHours(1)
        val alarm = baseAlarmNonRepeating.copy(
            enabled = false,
            dateTime = alarmTime,
            snoozeDateTime = alarmTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong()),
            weeklyRepeater = arbitraryWeeklyRepeater
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns alarm.dateTime
                assertFalse(alarm.isDirty())
            }
        }
    }

    @Test
    fun isDirty_ReturnsFalse_RepeatingAlarm_IsEnabled_AndSnoozed_AndInFuture_AndEqualsNextRepeating() {
        val alarmTime = now.plusHours(1)
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = alarmTime,
            snoozeDateTime = alarmTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong()),
            weeklyRepeater = arbitraryWeeklyRepeater
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns alarm.dateTime
                assertFalse(alarm.isDirty())
            }
        }
    }

    // Not Snoozed
    @Test
    fun isDirty_ReturnsTrue_RepeatingAlarm_IsEnabled_AndNotSnoozed_AndInPast() {
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = now.minusHours(1),
            weeklyRepeater = arbitraryWeeklyRepeater
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns alarm.dateTime
                assertTrue(alarm.isDirty())
            }
        }
    }

    @Test
    fun isDirty_ReturnsTrue_RepeatingAlarm_IsEnabled_AndNotSnoozed_AndIsNow() {
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = now,
            weeklyRepeater = arbitraryWeeklyRepeater
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns alarm.dateTime
                assertTrue(alarm.isDirty())
            }
        }
    }

    @Test
    fun isDirty_ReturnsTrue_RepeatingAlarm_IsEnabled_AndNotSnoozed_AndInFuture_AndIsBeforeNextRepeating() {
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = now.plusHours(1),
            weeklyRepeater = arbitraryWeeklyRepeater
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns alarm.dateTime.plusDays(1)
                assertTrue(alarm.isDirty())
            }
        }
    }

    @Test
    fun isDirty_ReturnsTrue_RepeatingAlarm_IsEnabled_AndNotSnoozed_AndInFuture_AndAfterNextRepeating() {
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = now.plusDays(1).plusHours(1),
            weeklyRepeater = arbitraryWeeklyRepeater
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns alarm.dateTime.minusDays(1)
                assertTrue(alarm.isDirty())
            }
        }
    }

    @Test
    fun isDirty_ReturnsFalse_RepeatingAlarm_IsDisabled_AndNotSnoozed() {
        val alarm = baseAlarmNonRepeating.copy(
            enabled = false,
            dateTime = now.plusHours(1),
            weeklyRepeater = arbitraryWeeklyRepeater
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns alarm.dateTime
                assertFalse(alarm.isDirty())
            }
        }
    }

    @Test
    fun isDirty_ReturnsFalse_RepeatingAlarm_IsEnabled_AndNotSnoozed_AndInFuture_AndEqualsNextRepeating() {
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = now.plusHours(1),
            weeklyRepeater = arbitraryWeeklyRepeater
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns alarm.dateTime
                assertFalse(alarm.isDirty())
            }
        }
    }

    /*
     * isDirty - Non-repeating Alarm
     */

    // Snoozed
    @Test
    fun isDirty_ReturnsTrue_NonRepeatingAlarm_IsEnabled_AndSnoozed_AndInPast() {
        val alarmTime = now.minusHours(1)
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = alarmTime,
            snoozeDateTime = alarmTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong())
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            assertTrue(alarm.isDirty())
        }
    }

    @Test
    fun isDirty_ReturnsTrue_NonRepeatingAlarm_IsEnabled_AndSnoozed_AndIsNow() {
        val alarmTime = now.minusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong())
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = alarmTime,
            snoozeDateTime = alarmTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong())
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            assertTrue(alarm.isDirty())
        }
    }

    @Test
    fun isDirty_ReturnsFalse_NonRepeatingAlarm_IsDisabled_AndSnoozed() {
        val alarmTime = now.plusHours(1)
        val alarm = baseAlarmNonRepeating.copy(
            enabled = false,
            dateTime = alarmTime,
            snoozeDateTime = alarmTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong())
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            assertFalse(alarm.isDirty())
        }
    }

    @Test
    fun isDirty_ReturnsFalse_NonRepeatingAlarm_IsEnabled_AndSnoozed_AndInFuture() {
        val alarmTime = now.plusHours(1)
        val alarm = baseAlarmNonRepeating.copy(
            dateTime = alarmTime,
            snoozeDateTime = alarmTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong())
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            assertFalse(alarm.isDirty())
        }
    }

    // Not Snoozed
    @Test
    fun isDirty_ReturnsTrue_NonRepeatingAlarm_IsEnabled_AndNotSnoozed_AndInPast() {
        val alarm = baseAlarmNonRepeating.copy(dateTime = now.minusHours(1))

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            assertTrue(alarm.isDirty())
        }
    }

    @Test
    fun isDirty_ReturnsTrue_NonRepeatingAlarm_IsEnabled_AndNotSnoozed_AndIsNow() {
        val alarm = baseAlarmNonRepeating.copy(dateTime = now)

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            assertTrue(alarm.isDirty())
        }
    }

    @Test
    fun isDirty_ReturnsFalse_NonRepeatingAlarm_IsDisabled_AndNotSnoozed() {
        val alarm = baseAlarmNonRepeating.copy(
            enabled = false,
            dateTime = now.plusHours(1)
        )

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            assertFalse(alarm.isDirty())
        }
    }

    @Test
    fun isDirty_ReturnsFalse_NonRepeatingAlarm_IsEnabled_AndNotSnoozed_AndInFuture() {
        val alarm = baseAlarmNonRepeating.copy(dateTime = now.plusHours(1))

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns now
            assertFalse(alarm.isDirty())
        }
    }

    /*
     * getRingtone
     */

    @Test
    fun getRingtone_ReturnsProperRingtone() {
        val alarm = baseAlarmNonRepeating
        val expectedRingtone = mockk<Ringtone>()

        mockkConstructor(RingtoneRepository::class) {
            every {
                anyConstructed<RingtoneRepository>().getRingtone(alarm.ringtoneUri)
            } returns expectedRingtone
            assertEquals(expectedRingtone, alarm.getRingtone(mockk<Context>()))
        }
    }

    /*
     * toCountdownString
     */

    @Test
    fun toCountdownString_ReturnsProperString() {
        val alarm = baseAlarmNonRepeating
        val expectedCountdownString = "expectedCountdownString"

        mockkObject(AlarmStringFormatter.Countdown) {
            every { AlarmStringFormatter.Countdown.format(any(), any()) } returns expectedCountdownString
            assertEquals(expectedCountdownString, alarm.toCountdownString(mockk<Context>()))
        }
    }

    /*
     * toScheduleString
     */

    @Test
    fun toScheduleString_ReturnsProperString() {
        val alarm = baseAlarmNonRepeating
        val expectedScheduleString = "expectedScheduleString"

        mockkObject(AlarmStringFormatter.ScheduleConfirmation) {
            every { AlarmStringFormatter.ScheduleConfirmation.format(any(), any()) } returns expectedScheduleString
            assertEquals(expectedScheduleString, alarm.toScheduleString(mockk<Context>()))
        }
    }
}
