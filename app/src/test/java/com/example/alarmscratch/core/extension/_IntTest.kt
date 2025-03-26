package com.example.alarmscratch.core.extension

import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("ClassName")
class _IntTest {

    /*
     * toOrdinal
     */

    @Test
    fun toOrdinal_ReturnsProperString_ForSingleDigitNumbers() {
        assertEquals("0th", 0.toOrdinal())
        assertEquals("1st", 1.toOrdinal())
        assertEquals("2nd", 2.toOrdinal())
        assertEquals("3rd", 3.toOrdinal())
        assertEquals("4th", 4.toOrdinal())
        assertEquals("5th", 5.toOrdinal())
        assertEquals("6th", 6.toOrdinal())
        assertEquals("7th", 7.toOrdinal())
        assertEquals("8th", 8.toOrdinal())
        assertEquals("9th", 9.toOrdinal())
    }

    @Test
    fun toOrdinal_ReturnsProperString_ForMultiDigitNumbers_WhereSecondToLastDigitIsNot1() {
        assertEquals("20th", 20.toOrdinal())
        assertEquals("31st", 31.toOrdinal())
        assertEquals("42nd", 42.toOrdinal())
        assertEquals("53rd", 53.toOrdinal())
        assertEquals("64th", 64.toOrdinal())
        assertEquals("75th", 75.toOrdinal())
        assertEquals("86th", 86.toOrdinal())
        assertEquals("97th", 97.toOrdinal())
        assertEquals("28th", 28.toOrdinal())
        assertEquals("39th", 39.toOrdinal())
    }

    @Test
    fun toOrdinal_ReturnsProperString_ForMultiDigitNumbers_WhenSecondToLastDigitIs1() {
        assertEquals("10th", 10.toOrdinal())
        assertEquals("11th", 11.toOrdinal())
        assertEquals("12th", 12.toOrdinal())
        assertEquals("13th", 13.toOrdinal())
        assertEquals("14th", 14.toOrdinal())
        assertEquals("15th", 15.toOrdinal())
        assertEquals("16th", 16.toOrdinal())
        assertEquals("17th", 17.toOrdinal())
        assertEquals("18th", 18.toOrdinal())
        assertEquals("19th", 19.toOrdinal())
    }
}
