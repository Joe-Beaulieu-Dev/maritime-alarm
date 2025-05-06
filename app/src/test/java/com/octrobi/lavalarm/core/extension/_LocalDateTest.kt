package com.octrobi.lavalarm.core.extension

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
        val date = LocalDateUtil.fromUtcMillis(1742947200000L)

        assertEquals(3, date.monthValue)
        assertEquals(26, date.dayOfMonth)
        assertEquals(2025, date.year)
    }

    /*
     * toUtcMillis
     */

    @Test
    fun toUtcMillis_ReturnsProperMilliseconds() {
        val date = LocalDate.of(2025, 3, 26)
        val expectedMillis = 1742947200000L

        assertEquals(expectedMillis, date.toUtcMillis())
    }
}
