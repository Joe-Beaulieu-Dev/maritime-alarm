package com.octrobi.lavalarm.alarm.util

import com.octrobi.lavalarm.alarm.data.model.WeeklyRepeater
import com.octrobi.lavalarm.core.extension.LocalDateTimeUtil
import com.octrobi.lavalarm.testutil.callPrivateFunction
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

class AlarmUtilTest {

    companion object {
        // Function names
        private const val SORT_REPEATING_DAYS_BY_PREFERENCE_FUNCTION = "sortRepeatingDaysByPreference"
    }

    /*
     * nextRepeatingDateTime
     */

    @Test
    fun nextRepeatingDateTime_IsToday_WhenAlarmIsInFuture_AndWeeklyRepeaterIsYesterdayAndToday() {
        val weeklyRepeater = WeeklyRepeater()
            .withDay(WeeklyRepeater.Day.TUESDAY)
            .withDay(WeeklyRepeater.Day.WEDNESDAY)
        val alarmDateTime = nextWeekOnDayAtNoon(DayOfWeek.WEDNESDAY)
        var nextRepeatingDateTime: LocalDateTime? = null

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarmDateTime.minusHours(1)
            nextRepeatingDateTime = AlarmUtil.nextRepeatingDateTime(alarmDateTime, weeklyRepeater)
        }

        assertEquals(alarmDateTime.dayOfYear, nextRepeatingDateTime!!.dayOfYear)
        assertEquals(alarmDateTime.toLocalTime(), nextRepeatingDateTime!!.toLocalTime())
    }

    @Test
    fun nextRepeatingDateTime_IsToday_WhenAlarmIsInFuture_AndWeeklyRepeaterIsToday() {
        val weeklyRepeater = WeeklyRepeater().withDay(WeeklyRepeater.Day.WEDNESDAY)
        val alarmDateTime = nextWeekOnDayAtNoon(DayOfWeek.WEDNESDAY)
        var nextRepeatingDateTime: LocalDateTime? = null

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarmDateTime.minusHours(1)
            nextRepeatingDateTime = AlarmUtil.nextRepeatingDateTime(alarmDateTime, weeklyRepeater)
        }

        assertEquals(alarmDateTime.dayOfYear, nextRepeatingDateTime!!.dayOfYear)
        assertEquals(alarmDateTime.toLocalTime(), nextRepeatingDateTime!!.toLocalTime())
    }

    @Test
    fun nextRepeatingDateTime_IsToday_WhenAlarmIsInFuture_AndWeeklyRepeaterIsTodayAndTomorrow() {
        val weeklyRepeater = WeeklyRepeater()
            .withDay(WeeklyRepeater.Day.WEDNESDAY)
            .withDay(WeeklyRepeater.Day.THURSDAY)
        val alarmDateTime = nextWeekOnDayAtNoon(DayOfWeek.WEDNESDAY)
        var nextRepeatingDateTime: LocalDateTime? = null

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarmDateTime.minusHours(1)
            nextRepeatingDateTime = AlarmUtil.nextRepeatingDateTime(alarmDateTime, weeklyRepeater)
        }

        assertEquals(alarmDateTime.dayOfYear, nextRepeatingDateTime!!.dayOfYear)
        assertEquals(alarmDateTime.toLocalTime(), nextRepeatingDateTime!!.toLocalTime())
    }

    @Test
    fun nextRepeatingDateTime_IsToday_WhenAlarmIsInFutureOrPast_AndWeeklyRepeaterIsEmpty() {
        val weeklyRepeater = WeeklyRepeater()
        val alarmDateTime = nextWeekOnDayAtNoon(DayOfWeek.WEDNESDAY)
        var nextRepeatingDateTime: LocalDateTime? = null

        // Alarm in Future
        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarmDateTime.minusHours(1)
            nextRepeatingDateTime = AlarmUtil.nextRepeatingDateTime(alarmDateTime, weeklyRepeater)
        }
        assertEquals(alarmDateTime.dayOfYear, nextRepeatingDateTime!!.dayOfYear)
        assertEquals(alarmDateTime.toLocalTime(), nextRepeatingDateTime!!.toLocalTime())

        // Alarm in Past
        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarmDateTime.plusHours(1)
            nextRepeatingDateTime = AlarmUtil.nextRepeatingDateTime(alarmDateTime, weeklyRepeater)
        }
        assertEquals(alarmDateTime.dayOfYear, nextRepeatingDateTime!!.dayOfYear)
        assertEquals(alarmDateTime.toLocalTime(), nextRepeatingDateTime!!.toLocalTime())
    }

    @Test
    fun nextRepeatingDateTime_IsInFuture_WhenAlarmIsInFuture_AndWeeklyRepeaterIsInPast() {
        val weeklyRepeater = WeeklyRepeater().withDay(WeeklyRepeater.Day.TUESDAY)
        val alarmDateTime = nextWeekOnDayAtNoon(DayOfWeek.WEDNESDAY)
        var nextRepeatingDateTime: LocalDateTime? = null

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarmDateTime.minusHours(1)
            nextRepeatingDateTime = AlarmUtil.nextRepeatingDateTime(alarmDateTime, weeklyRepeater)
        }

        assertEquals(alarmDateTime.dayOfYear + 6, nextRepeatingDateTime!!.dayOfYear)
        assertEquals(alarmDateTime.toLocalTime(), nextRepeatingDateTime!!.toLocalTime())
    }

    @Test
    fun nextRepeatingDateTime_IsInFuture_WhenAlarmIsInPast_AndWeeklyRepeaterIsToday() {
        val weeklyRepeater = WeeklyRepeater().withDay(WeeklyRepeater.Day.WEDNESDAY)
        val alarmDateTime = nextWeekOnDayAtNoon(DayOfWeek.WEDNESDAY)
        var nextRepeatingDateTime: LocalDateTime? = null

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarmDateTime.plusHours(1)
            nextRepeatingDateTime = AlarmUtil.nextRepeatingDateTime(alarmDateTime, weeklyRepeater)
        }

        assertEquals(alarmDateTime.dayOfYear + 7, nextRepeatingDateTime!!.dayOfYear)
        assertEquals(alarmDateTime.toLocalTime(), nextRepeatingDateTime!!.toLocalTime())
    }

    @Test
    fun nextRepeatingDateTime_IsInFuture_WhenAlarmIsInFuture_AndWeeklyRepeaterIsTomorrow() {
        val weeklyRepeater = WeeklyRepeater().withDay(WeeklyRepeater.Day.THURSDAY)
        val alarmDateTime = nextWeekOnDayAtNoon(DayOfWeek.WEDNESDAY)
        var nextRepeatingDateTime: LocalDateTime? = null

        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns alarmDateTime.minusHours(1)
            nextRepeatingDateTime = AlarmUtil.nextRepeatingDateTime(alarmDateTime, weeklyRepeater)
        }

        assertEquals(alarmDateTime.dayOfYear + 1, nextRepeatingDateTime!!.dayOfYear)
        assertEquals(alarmDateTime.toLocalTime(), nextRepeatingDateTime!!.toLocalTime())
    }

    /*
     * sortRepeatingDaysByPreference
     */

    @Test
    fun sortRepeatingDaysByPreference_GivesProperOrder() {
        val originalList = listOf(
            WeeklyRepeater.Day.TUESDAY,
            WeeklyRepeater.Day.WEDNESDAY,
            WeeklyRepeater.Day.THURSDAY
        )
        val expectedList = listOf(
            WeeklyRepeater.Day.WEDNESDAY,
            WeeklyRepeater.Day.THURSDAY,
            WeeklyRepeater.Day.TUESDAY
        )

        val actualList = AlarmUtil.callPrivateFunction(
            SORT_REPEATING_DAYS_BY_PREFERENCE_FUNCTION,
            DayOfWeek.WEDNESDAY,
            originalList
        )

        assertEquals(expectedList, actualList)
    }

    /*
     * Helper Functions
     */

    // Tests in this class require LocalDateTimes that are set on a specific DayOfWeek.
    // The only way to do this is via a TemporalAdjuster and finding the next occurrence
    // of the target DayOfWeek. Since the TemporalAdjuster could return days that are either in this week
    // or next week, for consistency we are ensuring that the returned LocalDateTime is always in next week.
    @Suppress("SameParameterValue")
    private fun nextWeekOnDayAtNoon(targetDayOfWeek: DayOfWeek): LocalDateTime {
        val now = LocalDateTime.now()
        val todayDayOfWeek = now.dayOfWeek
        val nextWeekOnTargetDay = now
            .withHour(12)
            .withSecond(0)
            .withNano(0)
            .with(TemporalAdjusters.next(targetDayOfWeek))

        // We want to ensure that nextWeekOnTargetDay is set for next week.
        // TemporalAdjusters.next(targetDayOfWeek) returns the next occurrence of targetDayOfWeek AFTER today.
        // This means that any targetDayOfWeek ON OR BEFORE today will ALREADY have one week added to it.
        //
        // However, any targetDayOfWeek AFTER today will not ALREADY have one week added to it,
        // which would mean that nextWeekOnTargetDay would CURRENTLY be set for this week in this case.
        // Therefore, we must add 7 days to the returned value if targetDayOfWeek is greater than today.
        //
        // Special case:
        // java.time.DayOfWeek starts the week on Monday. Therefore, if targetDayOfWeek is after today,
        // AND targetDayOfWeek is Sunday, then nextWeekOnTargetDay will ALREADY be set for next week.
        // Therefore, we do not have to add 7 days to it.
        return if (targetDayOfWeek > todayDayOfWeek) {
            // targetDayOfWeek is after todayDayOfWeek, therefore nextWeekOnTargetDay MAY currently be set in this week.
            // Add a week if necessary to ensure that we're returning a day in next week.
            if (targetDayOfWeek != DayOfWeek.SUNDAY) {
                // nextWeekOnTargetDay is CURRENTLY set for this week, add 7 days
                nextWeekOnTargetDay.plusDays(7)
            } else {
                // targetDayOfWeek > todayDayOfWeek AND targetDayOfWeek == DayOfWeek.SUNDAY,
                // therefore nextWeekOnTargetDay is ALREADY set in next week.
                nextWeekOnTargetDay
            }
        } else {
            // targetDayOfWeek is before or the same as todayDayOfWeek,
            // therefore nextWeekOnTargetDay is ALREADY set in next week.
            nextWeekOnTargetDay
        }
    }
}
