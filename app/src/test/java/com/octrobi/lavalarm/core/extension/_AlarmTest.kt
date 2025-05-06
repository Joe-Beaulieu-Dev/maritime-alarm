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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

@Suppress("ClassName")
class _AlarmTest {

    // Alarm
    private val baseAlarmNonRepeating = Alarm(
        id = 1,
        name = "name",
        enabled = true,
        dateTime = LocalDateTime.now(),
        weeklyRepeater = WeeklyRepeater(),
        ringtoneUri = "ringtoneUri",
        isVibrationEnabled = false,
        snoozeDateTime = null,
        snoozeDuration = 10
    )
    private val snoozedBaseAlarmNonRepeating = baseAlarmNonRepeating.copy(
        snoozeDateTime = baseAlarmNonRepeating.dateTime.plusMinutes(baseAlarmNonRepeating.snoozeDuration.toLong())
    )

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
        val alarm = snoozedBaseAlarmNonRepeating
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
    fun withFuturizedDateTime_ReturnsNextRepeatingDateTime_WhenAlarmIsRepeating_AndAlarmIsInPast() {
        val alarm = baseAlarmNonRepeating.copy(weeklyRepeater = WeeklyRepeater().withDay(WeeklyRepeater.Day.WEDNESDAY))
        val expectedDateTime = alarm.dateTime.plusDays(1)
        val expectedAlarm = alarm.copy(dateTime = expectedDateTime)

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarm.dateTime.plusHours(1)
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns expectedDateTime
                assertEquals(expectedAlarm, alarm.withFuturizedDateTime())
            }
        }
    }

    @Test
    fun withFuturizedDateTime_ReturnsNextRepeatingDateTime_WhenAlarmIsRepeating_AndAlarmIsNow() {
        val alarm = baseAlarmNonRepeating.copy(weeklyRepeater = WeeklyRepeater().withDay(WeeklyRepeater.Day.WEDNESDAY))
        val expectedDateTime = alarm.dateTime.plusDays(1)
        val expectedAlarm = alarm.copy(dateTime = expectedDateTime)

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarm.dateTime
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns expectedDateTime
                assertEquals(expectedAlarm, alarm.withFuturizedDateTime())
            }
        }
    }

    @Test
    fun withFuturizedDateTime_ReturnsSameDateTime_WhenAlarmIsRepeating_AndAlarmIsInFuture() {
        val alarm = baseAlarmNonRepeating.copy(weeklyRepeater = WeeklyRepeater().withDay(WeeklyRepeater.Day.WEDNESDAY))
        val expectedAlarm = alarm.copy()

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarm.dateTime.minusHours(1)
            assertEquals(expectedAlarm, alarm.withFuturizedDateTime())
        }
    }

    @Test
    fun withFuturizedDateTime_ReturnsAlarmDateTimePlusOneDay_WhenAlarmIsNotRepeating_AndAlarmIsInPast() {
        val alarm = baseAlarmNonRepeating
        val expectedDateTime = alarm.dateTime.plusDays(1)
        val expectedAlarm = alarm.copy(dateTime = expectedDateTime)

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarm.dateTime.plusHours(1)
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns expectedDateTime
                assertEquals(expectedAlarm, alarm.withFuturizedDateTime())
            }
        }
    }

    @Test
    fun withFuturizedDateTime_ReturnsAlarmDateTimePlusOneDay_WhenAlarmIsNotRepeating_AndAlarmIsNow() {
        val alarm = baseAlarmNonRepeating
        val expectedDateTime = alarm.dateTime.plusDays(1)
        val expectedAlarm = alarm.copy(dateTime = expectedDateTime)

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarm.dateTime
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns expectedDateTime
                assertEquals(expectedAlarm, alarm.withFuturizedDateTime())
            }
        }
    }

    @Test
    fun withFuturizedDateTime_ReturnsSameDateTime_WhenAlarmIsNotRepeating_AndAlarmIsInFuture() {
        val alarm = baseAlarmNonRepeating
        val expectedAlarm = alarm.copy()

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarm.dateTime.minusHours(1)
            assertEquals(expectedAlarm, alarm.withFuturizedDateTime())
        }
    }

    /*
     * isRepeating
     */

    @Test
    fun isRepeating_ReturnsTrue_WhenAlarmIsRepeating() {
        assertTrue(
            baseAlarmNonRepeating.copy(
                weeklyRepeater = WeeklyRepeater().withDay(WeeklyRepeater.Day.WEDNESDAY)
            ).isRepeating()
        )
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
        assertTrue(snoozedBaseAlarmNonRepeating.isSnoozed())
    }

    @Test
    fun isSnoozed_ReturnsFalse_WhenAlarmIsNotSnoozed() {
        assertFalse(baseAlarmNonRepeating.isSnoozed())
    }

    /*
     * isDirty
     */

    @Test
    fun isDirty_ReturnsTrue_WhenAlarmIsEnabled_AndSnoozed_AndInPast() {
        val alarm = snoozedBaseAlarmNonRepeating

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarm.snoozeDateTime!!.plusHours(1)
            assertTrue(alarm.isDirty())
        }
    }

    @Test
    fun isDirty_ReturnsTrue_WhenAlarmIsEnabled_AndSnoozed_AndIsNow() {
        val alarm = snoozedBaseAlarmNonRepeating

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarm.snoozeDateTime!!
            assertTrue(alarm.isDirty())
        }
    }

    @Test
    fun isDirty_ReturnsFalse_WhenAlarmIsEnabled_AndSnoozed_AndInFuture() {
        val alarm = snoozedBaseAlarmNonRepeating

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarm.snoozeDateTime!!.minusHours(1)
            assertFalse(alarm.isDirty())
        }
    }

    @Test
    fun isDirty_ReturnsTrue_WhenAlarmIsEnabled_AndNotSnoozed_AndInPast() {
        val alarm = baseAlarmNonRepeating

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarm.dateTime.plusHours(1)
            assertTrue(alarm.isDirty())
        }
    }

    @Test
    fun isDirty_ReturnsTrue_WhenAlarmIsEnabled_AndNotSnoozed_AndIsNow() {
        val alarm = baseAlarmNonRepeating

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarm.dateTime
            assertTrue(alarm.isDirty())
        }
    }

    @Test
    fun isDirty_ReturnsFalse_WhenAlarmIsEnabled_AndNotSnoozed_AndInFuture() {
        val alarm = baseAlarmNonRepeating

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarm.dateTime.minusHours(1)
            assertFalse(alarm.isDirty())
        }
    }

    @Test
    fun isDirty_ReturnsFalse_WhenAlarmIsDisabled_AndSnoozedOrNotSnoozed_AndInFuture() {
        val snoozedAlarm = snoozedBaseAlarmNonRepeating.copy(enabled = false)
        val alarm = baseAlarmNonRepeating.copy(enabled = false)

        mockkObject(LocalDateTimeUtil) {
            // Snoozed
            every { LocalDateTimeUtil.nowTruncated() } returns snoozedAlarm.snoozeDateTime!!.minusHours(1)
            assertFalse(snoozedAlarm.isDirty())

            // Not snoozed
            every { LocalDateTimeUtil.nowTruncated() } returns alarm.dateTime.minusHours(1)
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
