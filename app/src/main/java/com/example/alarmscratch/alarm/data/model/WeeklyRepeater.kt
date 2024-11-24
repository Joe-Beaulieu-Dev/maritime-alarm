package com.example.alarmscratch.alarm.data.model

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.LightVolcanicRock
import com.example.alarmscratch.core.ui.theme.WayDarkerBoatSails

class WeeklyRepeater(encodedRepeatingDays: Int = 0) {

    enum class Day(
        val mask: Int,
        val oneLetterShorthand: String,
        val threeLetterShorthand: String
    ) {
        SUNDAY(mask = 1, oneLetterShorthand = "S", threeLetterShorthand = "Sun"),
        MONDAY(mask = 2, oneLetterShorthand = "M", threeLetterShorthand = "Mon"),
        TUESDAY(mask = 4, oneLetterShorthand = "T", threeLetterShorthand = "Tue"),
        WEDNESDAY(mask = 8, oneLetterShorthand = "W", threeLetterShorthand = "Wed"),
        THURSDAY(mask = 16, oneLetterShorthand = "T", threeLetterShorthand = "Thu"),
        FRIDAY(mask = 32, oneLetterShorthand = "F", threeLetterShorthand = "Fri"),
        SATURDAY(mask = 64, oneLetterShorthand = "S", threeLetterShorthand = "Sat");

        fun dayNumber(): Int = this.ordinal + 1
    }

    private val repeatingDayMap: MutableMap<Day, Boolean> =
        mutableMapOf<Day, Boolean>()
            .also { map ->
                // Add each Day to Map, and assign true/false based on
                // whether or not the Day is included in encodedDays
                Day.entries.forEach { day ->
                    map[day] = encodedRepeatingDays.and(day.mask) == day.mask
                }
            }

    /*
     * Query
     */

    fun hasRepeatingDays(): Boolean =
        repeatingDayMap.containsValue(true)

    fun isRepeatingOn(day: Day): Boolean =
        repeatingDayMap[day] ?: false

    fun getRepeatingDays(): List<Day> =
        repeatingDayMap.filterValues { it }.keys.toList()

    /*
     * Manipulate
     */

    fun withDay(day: Day): WeeklyRepeater =
        if (repeatingDayMap[day] == false) {
            WeeklyRepeater(toEncodedRepeatingDays() + day.mask)
        } else {
            WeeklyRepeater(toEncodedRepeatingDays())
        }

    fun withoutDay(day: Day): WeeklyRepeater =
        if (repeatingDayMap[day] == true) {
            WeeklyRepeater(toEncodedRepeatingDays() - day.mask)
        } else {
            WeeklyRepeater(toEncodedRepeatingDays())
        }

    /*
     * Transform
     */

    fun toEncodedRepeatingDays(): Int =
        repeatingDayMap.filterValues { it }.keys.sumOf { it.mask }

    fun toAlarmCardDateAnnotatedString(enabled: Boolean): AnnotatedString =
        buildAnnotatedString {
            // Add all Days to the AnnotatedString with different
            // styles based on whether or not they're set to repeat
            repeatingDayMap.forEach { (day, isRepeating) ->
                if (isRepeating) {
                    // Day set to repeat, make it stand out
                    withStyle(style = SpanStyle(color = if (enabled) BoatSails else WayDarkerBoatSails)) {
                        append(day.oneLetterShorthand)
                    }
                } else {
                    // Day not set to repeat, make it appear less prominent
                    withStyle(style = SpanStyle(color = LightVolcanicRock)) {
                        append(day.oneLetterShorthand)
                    }
                }
                // Conditionally add spacing for style
                if (day != Day.SATURDAY) {
                    append(" ")
                }
            }
        }

    fun toAlarmCreationDateString(): String =
        repeatingDayMap
            .filter { it.value }
            .map { it.key.threeLetterShorthand }
            .joinToString(separator = ", ")
}
