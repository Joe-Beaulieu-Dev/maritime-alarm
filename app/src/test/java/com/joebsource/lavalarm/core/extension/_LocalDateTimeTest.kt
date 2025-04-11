package com.joebsource.lavalarm.core.extension

import android.content.Context
import com.joebsource.lavalarm.R
import com.joebsource.lavalarm.testutil.ReflectionUtil
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

@Suppress("ClassName")
class _LocalDateTimeTest {

    companion object {
        // File names
        private const val LOCAL_DATE_TIME_EXTENSION_FILE = "com.joebsource.lavalarm.core.extension._LocalDateTimeKt"
        // Function names
        private const val FORMAT_CALENDAR_DATE_FUNCTION = "formatCalendarDate"
        private const val GET_FORMATTED_MINUTE_FUNCTION = "getFormattedMinute"
    }

    /*
     * nowTruncated
     */

    @Test
    fun nowTruncated_ReturnsTruncatedLocalDateTime() {
        val localDateTime = LocalDateTimeUtil.nowTruncated()

        assertEquals(0, localDateTime.second)
        assertEquals(0, localDateTime.nano)
    }

    /*
     * zonedEpochMillis
     */

    @Test
    fun zonedEpochMillis_ReturnsCorrectZonedMillis() {
        val dateTime = LocalDateTime.now()
        val zoneId = ZoneId.of("America/New_York")
        val expectedMillis = dateTime.atZone(zoneId).toEpochSecond() * 1000

        mockkStatic(ZoneId::class) {
            every { ZoneId.systemDefault() } returns zoneId
            assertEquals(expectedMillis, dateTime.zonedEpochMillis())
        }
    }

    /*
     * getDayFull
     */

    @Test
    fun getDayFull_ReturnsProperString() {
        val dateTime = dateTimeOnDayAtNoon(DayOfWeek.WEDNESDAY)
        val expectedDay = "Wednesday"

        assertEquals(expectedDay, dateTime.getDayFull())
    }

    /*
     * getDayShorthand
     */

    @Test
    fun getDayShorthand_ReturnsProperString() {
        val dateTime = dateTimeOnDayAtNoon(DayOfWeek.WEDNESDAY)
        val expectedDay = "Wed"

        assertEquals(expectedDay, dateTime.getDayShorthand())
    }

    /*
     * toAlarmDateString
     */

    @Test
    fun toAlarmDateString_ReturnsProperString_ForToday() {
        val dateTime = dateTimeOnDayAtNoon(DayOfWeek.WEDNESDAY)
        val expectedString = "Today"
        val context = mockk<Context> {
            every { getString(R.string.date_today) } returns expectedString
        }

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns dateTime
            assertEquals(expectedString, dateTime.toAlarmDateString(context))
        }
    }

    @Test
    fun toAlarmDateString_ReturnsProperString_ForTomorrow() {
        val dateTime = dateTimeOnDayAtNoon(DayOfWeek.WEDNESDAY)
        val expectedString = "Tomorrow"
        val context = mockk<Context> {
            every { getString(R.string.date_tomorrow) } returns expectedString
        }

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns dateTime.minusDays(1)
            assertEquals(expectedString, dateTime.toAlarmDateString(context))
        }
    }

    @Test
    fun toAlarmDateString_ReturnsProperString_ForDayBeyondTomorrow() {
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.NOON
        )
        val expectedString = "Mon, Mar 24th 2025"

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns dateTime.minusDays(2)
            assertEquals(expectedString, dateTime.toAlarmDateString(mockk<Context>()))
        }
    }

    @Test
    fun toAlarmDateString_ReturnsProperString_ForDayBeforeToday() {
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.NOON
        )
        val expectedString = "Mon, Mar 24th 2025"

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns dateTime.plusDays(2)
            assertEquals(expectedString, dateTime.toAlarmDateString(mockk<Context>()))
        }
    }

    /*
     * formatCalendarDate
     */

    @Test
    fun formatCalendarDate_ReturnsProperString() {
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.NOON
        )
        val expectedString = "Mon, Mar 24th 2025"

        val actualString = ReflectionUtil.callPrivateTopLevelFunction(
            LOCAL_DATE_TIME_EXTENSION_FILE,
            FORMAT_CALENDAR_DATE_FUNCTION,
            dateTime.toLocalDate()
        )

        assertEquals(expectedString, actualString)
    }

    /*
     * to12HourNotificationDateTimeString
     */

    @Test
    fun to12HourNotificationDateTimeString_ReturnsProperString_WhenTimeIsAm() {
        val context = mockk<Context> {
            every { getString(R.string.time_am) } returns "AM"
        }
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.MIDNIGHT
        )
        val expectedString = "Mon, 12:00 AM"

        assertEquals(expectedString, dateTime.to12HourNotificationDateTimeString(context))
    }

    @Test
    fun to12HourNotificationDateTimeString_ReturnsProperString_WhenTimeIsPm() {
        val context = mockk<Context> {
            every { getString(R.string.time_pm) } returns "PM"
        }
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.NOON
        )
        val expectedString = "Mon, 12:00 PM"

        assertEquals(expectedString, dateTime.to12HourNotificationDateTimeString(context))
    }

    /*
     * to24HourNotificationDateTimeString
     */

    @Test
    fun to24HourNotificationDateTimeString_ReturnsProperString_WhenTimeIsBefore10Am() {
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.of(5, 0)
        )
        val expectedString = "Mon, 05:00"

        assertEquals(expectedString, dateTime.to24HourNotificationDateTimeString())
    }

    @Test
    fun to24HourNotificationDateTimeString_ReturnsProperString_WhenTimeIsAfter9Am() {
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.of(14, 0)
        )
        val expectedString = "Mon, 14:00"

        assertEquals(expectedString, dateTime.to24HourNotificationDateTimeString())
    }

    /*
     * get12HourTime
     */

    @Test
    fun get12HourTime_ReturnsProperString_WhenHourIs0() {
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.of(0, 30)
        )
        val expectedString = "12:30"

        assertEquals(expectedString, dateTime.get12HourTime())
    }

    @Test
    fun get12HourTime_ReturnsProperString_WhenHourIsGreaterThan0AndLessThanOrEqualTo12() {
        val dateTime1030 = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.of(10, 30)
        )
        val expectedString1030 = "10:30"
        val dateTime1230 = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.of(12, 30)
        )
        val expectedString1230 = "12:30"

        assertEquals(expectedString1030, dateTime1030.get12HourTime())
        assertEquals(expectedString1230, dateTime1230.get12HourTime())
    }

    @Test
    fun get12HourTime_ReturnsProperString_WhenHourIsGreaterThan12() {
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.of(14, 30)
        )
        val expectedString = "2:30"

        assertEquals(expectedString, dateTime.get12HourTime())
    }

    /*
     * get24HourTime
     */

    @Test
    fun get24HourTime_ReturnsProperString_WhenHourIsLessThan10() {
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.of(5, 30)
        )
        val expectedString = "05:30"

        assertEquals(expectedString, dateTime.get24HourTime())
    }

    @Test
    fun get24HourTime_ReturnsProperString_WhenHourIsGreaterThan9() {
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.of(21, 30)
        )
        val expectedString = "21:30"

        assertEquals(expectedString, dateTime.get24HourTime())
    }

    /*
     * getAmPm
     */

    @Test
    fun getAmPm_ReturnsProperString_WhenHourIsLessThan12() {
        val expectedString = "AM"
        val context = mockk<Context> {
            every { getString(R.string.time_am) } returns expectedString
        }
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.of(5, 30)
        )

        assertEquals(expectedString, dateTime.getAmPm(context))
    }

    @Test
    fun getAmPm_ReturnsProperString_WhenHourIsGreaterThan11() {
        val expectedString = "PM"
        val context = mockk<Context> {
            every { getString(R.string.time_pm) } returns expectedString
        }
        val dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 24),
            LocalTime.of(12, 30)
        )

        assertEquals(expectedString, dateTime.getAmPm(context))
    }

    /*
     * getFormattedMinute
     */

    @Test
    fun getFormattedMinute_ReturnsProperString_WhenMinuteIsLessThan10() {
        val time = LocalTime.of(12, 5)
        val expectedString = "05"

        val actualString = ReflectionUtil.callPrivateTopLevelFunction(
            LOCAL_DATE_TIME_EXTENSION_FILE,
            GET_FORMATTED_MINUTE_FUNCTION,
            time
        )

        assertEquals(expectedString, actualString)
    }

    @Test
    fun getFormattedMinute_ReturnsProperString_WhenMinuteIsGreaterThan9() {
        val time = LocalTime.of(12, 30)
        val expectedString = "30"

        val actualString = ReflectionUtil.callPrivateTopLevelFunction(
            LOCAL_DATE_TIME_EXTENSION_FILE,
            GET_FORMATTED_MINUTE_FUNCTION,
            time
        )

        assertEquals(expectedString, actualString)
    }

    /*
     * Helper Functions
     */

    @Suppress("SameParameterValue")
    private fun dateTimeOnDayAtNoon(targetDayOfWeek: DayOfWeek): LocalDateTime =
        LocalDateTime.now()
            .withHour(12)
            .withSecond(0)
            .withNano(0)
            .with(TemporalAdjusters.next(targetDayOfWeek))
}
