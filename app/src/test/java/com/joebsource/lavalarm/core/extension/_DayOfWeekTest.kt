package com.joebsource.lavalarm.core.extension

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek

@Suppress("ClassName")
class _DayOfWeekTest {

    /*
     * dayNumber
     */

    @Test
    fun dayNumber_ReturnsProperNumber_ForSunday() {
        assertEquals(1, DayOfWeek.SUNDAY.dayNumber())
    }

    @Test
    fun dayNumber_ReturnsProperNumber_ForMonday() {
        assertEquals(2, DayOfWeek.MONDAY.dayNumber())
    }

    @Test
    fun dayNumber_ReturnsProperNumber_ForTuesday() {
        assertEquals(3, DayOfWeek.TUESDAY.dayNumber())
    }

    @Test
    fun dayNumber_ReturnsProperNumber_ForWednesday() {
        assertEquals(4, DayOfWeek.WEDNESDAY.dayNumber())
    }

    @Test
    fun dayNumber_ReturnsProperNumber_ForThursday() {
        assertEquals(5, DayOfWeek.THURSDAY.dayNumber())
    }

    @Test
    fun dayNumber_ReturnsProperNumber_ForFriday() {
        assertEquals(6, DayOfWeek.FRIDAY.dayNumber())
    }

    @Test
    fun dayNumber_ReturnsProperNumber_ForSaturday() {
        assertEquals(7, DayOfWeek.SATURDAY.dayNumber())
    }
}
