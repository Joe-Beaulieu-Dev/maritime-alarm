package com.joebsource.lavalarm.alarm.alarmexecution

import com.joebsource.lavalarm.alarm.data.model.Alarm
import com.joebsource.lavalarm.alarm.data.model.WeeklyRepeater
import com.joebsource.lavalarm.alarm.data.repository.AlarmRepository
import com.joebsource.lavalarm.alarm.util.AlarmUtil
import com.joebsource.lavalarm.core.extension.isDirty
import com.joebsource.lavalarm.core.extension.isRepeating
import com.joebsource.lavalarm.testutil.callPrivateSuspendFunction
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

class AlarmSchedulerTest {

    // Alarm
    private val ringtoneUri = "ringtoneUri"
    private val snoozeDuration = 10

    companion object {
        // File names
        private const val ALARM_EXTENSION_FUNCTION_FILE = "com.joebsource.lavalarm.core.extension._AlarmKt"
        // Function names
        private const val CLEAN_ALARM_FUNCTION = "cleanAlarm"
    }

    /*
     * cleanAlarm
     */

    @Test
    fun cleanAlarm_Cleans_RepeatingAlarm() = runTest {
        val dateTime = nextWeekOnDayAtNoon(DayOfWeek.WEDNESDAY)
        val expectedDateTime = dateTime.plusDays(7)
        val alarm = Alarm(
            enabled = true,
            dateTime = dateTime,
            weeklyRepeater = WeeklyRepeater().withDay(WeeklyRepeater.Day.WEDNESDAY),
            ringtoneUri = ringtoneUri,
            snoozeDateTime = dateTime.plusMinutes(snoozeDuration.toLong()),
            snoozeDuration = snoozeDuration
        )
        val expectedAlarm = alarm.copy(
            dateTime = expectedDateTime,
            snoozeDateTime = null
        )

        val alarmRepository = mockk<AlarmRepository> { coEvery { updateAlarm(any()) } returns Unit }
        mockkStatic(ALARM_EXTENSION_FUNCTION_FILE) {
            every { (Alarm::isDirty)(any()) } returns true
            every { (Alarm::isRepeating)(any()) } returns true
            mockkObject(AlarmUtil) {
                every { AlarmUtil.nextRepeatingDateTime(any(), any()) } returns expectedDateTime
                AlarmScheduler.callPrivateSuspendFunction(CLEAN_ALARM_FUNCTION, alarm, alarmRepository)
            }
        }

        coVerify {
            alarmRepository.updateAlarm(expectedAlarm)
        }
    }

    @Test
    fun cleanAlarm_Cleans_NonRepeatingAlarm() = runTest {
        val dateTime = nextWeekOnDayAtNoon(DayOfWeek.WEDNESDAY)
        val alarm = Alarm(
            enabled = true,
            dateTime = dateTime,
            weeklyRepeater = WeeklyRepeater(),
            ringtoneUri = ringtoneUri,
            snoozeDateTime = dateTime.plusMinutes(snoozeDuration.toLong()),
            snoozeDuration = snoozeDuration
        )
        val expectedAlarm = alarm.copy(
            enabled = false,
            snoozeDateTime = null
        )

        val alarmRepository = mockk<AlarmRepository> { coEvery { updateAlarm(any()) } returns Unit }
        mockkStatic(ALARM_EXTENSION_FUNCTION_FILE) {
            every { (Alarm::isDirty)(any()) } returns true
            every { (Alarm::isRepeating)(any()) } returns false
            AlarmScheduler.callPrivateSuspendFunction(CLEAN_ALARM_FUNCTION, alarm, alarmRepository)
        }

        coVerify {
            alarmRepository.updateAlarm(expectedAlarm)
        }
    }

    @Test
    fun cleanAlarm_DoesNothing_IfAlarmIsAlreadyClean() = runTest {
        val dateTime = nextWeekOnDayAtNoon(DayOfWeek.WEDNESDAY)
        val alarm = Alarm(
            enabled = true,
            dateTime = dateTime,
            weeklyRepeater = WeeklyRepeater(),
            ringtoneUri = ringtoneUri,
            snoozeDateTime = dateTime.plusMinutes(snoozeDuration.toLong()),
            snoozeDuration = snoozeDuration
        )

        val alarmRepository = mockk<AlarmRepository>()
        mockkStatic(ALARM_EXTENSION_FUNCTION_FILE) {
            every { (Alarm::isDirty)(any()) } returns false
            AlarmScheduler.callPrivateSuspendFunction(CLEAN_ALARM_FUNCTION, alarm, alarmRepository)
        }

        coVerify {
            alarmRepository wasNot Called
        }
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
