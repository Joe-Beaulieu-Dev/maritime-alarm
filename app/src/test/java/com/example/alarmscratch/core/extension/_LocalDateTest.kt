package com.example.alarmscratch.core.extension

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

@Suppress("ClassName")
class _LocalDateTest {

    /*
     * fromUtcMillis
     */

    @Test
    fun fromUtcMillis_ReturnsProperDate() {
        val dateTime = LocalDateUtil.fromUtcMillis(1742947200000L)

        assertEquals(3, dateTime.monthValue)
        assertEquals(26, dateTime.dayOfMonth)
        assertEquals(2025, dateTime.year)
    }

    /*
     * toUtcMillis
     */

    @Test
    fun toUtcMillis_ReturnsProperMilliseconds() {
        val dateTime = LocalDate.of(2025, 3, 26)
        val expectedMillis = 1742947200000L

        assertEquals(expectedMillis, dateTime.toUtcMillis())
    }
}
